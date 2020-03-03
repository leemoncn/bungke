package com.leemon.wushiwan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import com.leemon.wushiwan.dto.PayPartner;
import com.leemon.wushiwan.entity.FinancialTrade;
import com.leemon.wushiwan.dto.PayPassbackParam;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import com.leemon.wushiwan.enums.PartnerTimeType;
import com.leemon.wushiwan.enums.PayType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.FinancialTradeMapper;
import com.leemon.wushiwan.pay.PayService;
import com.leemon.wushiwan.service.ICoreCurrencyChangeService;
import com.leemon.wushiwan.service.ICoreDepositPurchaseService;
import com.leemon.wushiwan.service.ICorePartnerPurchaseService;
import com.leemon.wushiwan.service.IFinancialTradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.RMBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-07-14
 */
@Service
@Slf4j
public class FinancialTradeServiceImpl extends ServiceImpl<FinancialTradeMapper, FinancialTrade> implements IFinancialTradeService {

	private final PayService payService;
	private final ICoreCurrencyChangeService currencyChangeService;
	@Autowired
	private ISysUserService sysUserService;
	private final ICoreDepositPurchaseService depositPurchaseService;
	private final ICorePartnerPurchaseService partnerPurchaseService;

	public FinancialTradeServiceImpl(PayService payService, ICoreCurrencyChangeService currencyChangeService, ICoreDepositPurchaseService depositPurchaseService, ICorePartnerPurchaseService partnerPurchaseService) {
		this.payService = payService;
		this.currencyChangeService = currencyChangeService;
		this.partnerPurchaseService = partnerPurchaseService;
		this.sysUserService = sysUserService;
		this.depositPurchaseService = depositPurchaseService;
	}

	/**
	 * 发起一笔支付
	 *
	 * @param userId
	 * @param amount
	 * @param payType
	 * @param data
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String startPay(Integer userId, Integer amount, PayType payType, String data, boolean isApp) {
		FinancialTrade ft = new FinancialTrade();
		String tradeNo = generateTradeNo(userId, amount, payType);
		ft.setTradeNo(tradeNo);
		ft.setUserId(userId);
		ft.setAmount(amount);
		ft.setTitle(payType.getTitle());
		ft.setDescription(String.format("%s%.02f元", payType.getTitle(), RMBUtil.fenToYuan(amount)));
		ft.setFinished(false);
		save(ft);
		PayPassbackParam param = new PayPassbackParam();
		param.setPayType(payType);
		param.setUserId(userId);
		param.setData(data);
		if (isApp) {
			return payService.appPay(tradeNo, amount, ft.getTitle(), ft.getDescription(), JSON.toJSONString(param));
		}
		return payService.h5Pay(tradeNo, amount, ft.getTitle(), ft.getDescription(), JSON.toJSONString(param));
	}

	/**
	 * 收到充值完成的回调,会自动添加一条流水
	 *
	 * @param tradeNo
	 * @param thirdTradeNo
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void finishTrade(String tradeNo, String thirdTradeNo, Integer amount, PayPassbackParam param) {
		QueryWrapper<FinancialTrade> qw = new QueryWrapper<>();
		qw.lambda().eq(FinancialTrade::getTradeNo, tradeNo);
		FinancialTrade ft = getOne(qw);
		if (ft == null) {
			throw new LogicException(ErrorCode.SYS_ERROR, "当前订单号不存在");
		}
		if (!amount.equals(ft.getAmount())) {//通知收到的订单金额和推单的金额不一致
			throw new LogicException(ErrorCode.PAY_ERROR, "支付宝通知的订单金额[%d]和数据库中存的推单的订单金额[%d]不一致，有可能是数据泄露导致其他人推过来的假通知", amount, ft.getAmount());
		}
		ft.setThirdTradeNo(thirdTradeNo);
		ft.setFinished(true);
		ft.setFinishTime(LocalDateTime.now());
		updateById(ft);
		CurrencyChangeReasonType changeReason;
		switch (param.getPayType()) {
			case RechargeDeposit://保证金
				changeReason = CurrencyChangeReasonType.RECHARGE_CASH_DEPOSIT;
				depositPurchaseService.rechargeDeposit(amount, param.getUserId());
				break;
			case RechargeMissionCoin://任务币
				changeReason = CurrencyChangeReasonType.RECHARGE_MISSION_COIN;
				sysUserService.updateUserMissionCoin(sysUserService.getById(param.getUserId()), changeReason, amount);
				break;
			case Partner://合作商
				changeReason = CurrencyChangeReasonType.BE_PARTNER;
				PayPartner pp = JSONObject.parseObject(param.getData(), PayPartner.class);
				partnerPurchaseService.addNewPartner(sysUserService.getById(param.getUserId()), pp.getPartnerId(), pp.getIsYear() ? PartnerTimeType.YEAH : PartnerTimeType.MONTH, null);
				break;
			default:
				throw new LogicException(ErrorCode.SYS_ERROR, "需要完成此类型相关代码");
		}
		//添加流水变动
		currencyChangeService.addCurrencyChangeRecord(param.getUserId(), CurrencyType.RMB, changeReason, amount);
	}

	/**
	 * 生成支付单号
	 *
	 * @param userId
	 * @param amount
	 * @param payType
	 * @return
	 */
	private String generateTradeNo(Integer userId, Integer amount, PayType payType) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String now = LocalDateTime.now().format(formatter);
		return String.format("%d_%d_%d_%s_%s", userId, payType.ordinal(), amount, now, RandomStringUtils.randomNumeric(10));
	}
}
