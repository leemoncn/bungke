package com.leemon.wushiwan.service.impl;

import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.entity.FinancialTransfer;
import com.leemon.wushiwan.entity.FinancialWithdrawal;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import com.leemon.wushiwan.enums.NoticeType;
import com.leemon.wushiwan.enums.SystemConfig;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.FinancialWithdrawalMapper;
import com.leemon.wushiwan.pay.PayService;
import com.leemon.wushiwan.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.RMBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-06-29
 */
@Service
@Slf4j
public class FinancialWithdrawalServiceImpl extends ServiceImpl<FinancialWithdrawalMapper, FinancialWithdrawal> implements IFinancialWithdrawalService {

	@Autowired
	private ISocialNoticeService noticeService;
	private final ICoreCurrencyChangeService currencyChangeService;
	private final PayService payService;
	private final IFinancialTransferService transferService;
	@Autowired
	private ISysUserService userService;
	private final ICoreAgencyService agencyService;
	private final ICorePartnerService partnerService;

	@Autowired
	private ICoreDepositPurchaseService depositPurchaseService;
	@Autowired
	private ISysUserService sysUserService;

	public FinancialWithdrawalServiceImpl(ICoreCurrencyChangeService currencyChangeService, PayService payService, IFinancialTransferService transferService, ICoreAgencyService agencyService, ICorePartnerService partnerService) {
		this.currencyChangeService = currencyChangeService;
		this.payService = payService;
		this.transferService = transferService;
		this.userService = userService;
		this.agencyService = agencyService;
		this.partnerService = partnerService;
	}

	/**
	 * 申请提现
	 *
	 * @param userId
	 * @param money            需要提现的人民币数值
	 * @param reasonPropertyId 提现原因
	 */
	@Override
	public void requestWithdrawal(Integer userId, Integer money, CurrencyChangeReasonType reasonPropertyId) {
		FinancialWithdrawal fw = new FinancialWithdrawal();
		fw.setUserId(userId);
		fw.setMoney(money);
		fw.setReasonPropertyId(reasonPropertyId);
		if (!save(fw)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "保存申请提现失败");
		}
		String msg = String.format("您的提现申请<%.02f元>已提交，请等待管理员审核", RMBUtil.fenToYuan(money));
		noticeService.sendNotice(NoticeType.WITHDRAWAL_REQUEST, msg, fw.getUserId());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void approveWithdrawal(Integer withdrawalId, Boolean result, SysUser approveUser) {

		FinancialWithdrawal fw = getById(withdrawalId);
		if (fw == null) {
			throw new LogicException(ErrorCode.SYS_ERROR, "操作的提现申请不存在，id = %d", withdrawalId);
		}
		fw.setApprove(result);
		fw.setApproveTime(LocalDateTime.now());
		fw.setApproveUserId(approveUser.getId());
		if (!updateById(fw)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新提现申请出错，id = %d", withdrawalId);
		}
		String msg;
		if (result) {
			this.onWithdrawalSuccess(fw);
			msg = String.format("您的提现申请审核成功，金额%.02f元", RMBUtil.fenToYuan(fw.getMoney()));
		} else {
			this.onWithdrawalFailed(fw);
			msg = "您的提现申请审核失败，请联系客服";
		}
		noticeService.sendNotice(NoticeType.WITHDRAWAL_REVIEW_FINISH, msg, fw.getUserId());
	}

	private void onWithdrawalFailed(FinancialWithdrawal fw) {
		SysUser user = userService.getById(fw.getUserId());
		//如果提现审核不通过的话，需要将钱退回
		if (fw.getReasonPropertyId() == CurrencyChangeReasonType.WITHDRAWAL_EARNING) {//提现收入
			CoreAgency ca = agencyService.getById(user.getAgencyId());
			//手续费分成
			int fee = BigDecimal.valueOf(ca.getWithdrawByUser()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(fw.getMoney())).intValue();
			userService.updateUserEarning(user, CurrencyChangeReasonType.WITHDRAWAL_EARNING_REJECT, fw.getMoney() + fee);
		}
		if (fw.getReasonPropertyId() == CurrencyChangeReasonType.WITHDRAWAL_MISSION_COIN) {//提现任务币
			CorePartner cp = partnerService.getById(user.getPartnerId());
			//扣除的手续费金额
			int fee = BigDecimal.valueOf(cp.getMissionPaymentPercent()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(fw.getMoney())).intValue();
			userService.updateUserMissionCoin(user, CurrencyChangeReasonType.WITHDRAWAL_MISSION_COIN_REJECT, fw.getMoney() + fee);
		}
		if (fw.getReasonPropertyId() == CurrencyChangeReasonType.WITHDRAWAL_CASH_DEPOSIT) {//提现保证金
			//保证金退款，扣除1%的手续费也要退回去
			int fee = fw.getMoney() * Integer.parseInt(SystemConfig.WITHDEAWAL_DEPOSIT_FEE.getPropertyValue()) / 100;
			userService.updateUserDeposit(user, fw.getMoney() + fee);
			currencyChangeService.addCurrencyChangeRecord(user.getId(), CurrencyType.DEPOSIT, CurrencyChangeReasonType.WITHDRAWAL_CASH_DEPOSIT_REJECT, fw.getMoney() + fee);
		}
	}

	private void onWithdrawalSuccess(FinancialWithdrawal fw) {
		//添加流水记录
		currencyChangeService.addCurrencyChangeRecord(fw.getUserId(), CurrencyType.RMB, fw.getReasonPropertyId(), -fw.getMoney());
		if (fw.getReasonPropertyId() == CurrencyChangeReasonType.WITHDRAWAL_CASH_DEPOSIT) {//保证金申退完成
			depositPurchaseService.refundDepositFinish(fw.getUserId());//更新保证金申退完成时间
		}
		if (fw.getReasonPropertyId() == CurrencyChangeReasonType.WITHDRAWAL_EARNING) {//提现收入审核通过，需要给上级返现
			SysUser user = userService.getById(fw.getUserId());
			//给上级添加收入返现
			Integer superiorId = user.getSuperiorId();
			CoreAgency ca = agencyService.getById(user.getAgencyId());
			int change = fw.getMoney();
			//手续费分成(负值)
			int fee = BigDecimal.valueOf(ca.getWithdrawByUser()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(change)).intValue();
			if (superiorId != null && fee != 0) {
				SysUser superiorUser = userService.getById(superiorId);
				userService.updateUserEarning(superiorUser, CurrencyChangeReasonType.EARNING_BY_SUBORDINATE_WITHDRAWAL_EARNING, Math.abs(fee));
			}
		}

		SysUser user = sysUserService.getById(fw.getUserId());

		//自己生成的转账单号
		String transferNo = generateTradeNo(fw.getUserId(), fw.getMoney(), fw.getReasonPropertyId());
		//支付宝的转账单号
		String alipayTransferNo = payService.transferToAccount(transferNo, user.getAlipay(), fw.getMoney(), user.getRealName(), "帮客提现" + RMBUtil.fenToYuan(fw.getMoney()) + "元");
		FinancialTransfer ft = null;
		try {
			ft = new FinancialTransfer();
			ft.setUserId(user.getId());
			ft.setThirdTransferNo(alipayTransferNo);
			ft.setTransferNo(transferNo);
			ft.setWithdrawalId(fw.getId());
			transferService.save(ft);
		} catch (Exception e) {
			log.error("保存转账记录时出错，ft = {}", ft);
		}
	}

	/**
	 * 生成转账单号
	 *
	 * @param userId
	 * @param amount
	 * @param reason
	 * @return
	 */
	private String generateTradeNo(Integer userId, Integer amount, CurrencyChangeReasonType reason) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String now = LocalDateTime.now().format(formatter);
		return String.format("%d_%d_%d_%s_%s", userId, reason.getValue(), amount, now, RandomStringUtils.randomNumeric(10));
	}
}
