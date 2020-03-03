package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.leemon.wushiwan.entity.CoreDepositPurchase;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import com.leemon.wushiwan.enums.SystemConfig;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreDepositPurchaseMapper;
import com.leemon.wushiwan.service.ICoreCurrencyChangeService;
import com.leemon.wushiwan.service.ICoreDepositPurchaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.service.IFinancialWithdrawalService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-07-01
 */
@Service
public class CoreDepositPurchaseServiceImpl extends ServiceImpl<CoreDepositPurchaseMapper, CoreDepositPurchase> implements ICoreDepositPurchaseService {

	@Autowired
	private IFinancialWithdrawalService withdrawalService;
	@Autowired
	private ICoreCurrencyChangeService currencyChangeService;
	@Autowired
	private ISysUserService sysUserService;

	/**
	 * 充值保证金
	 *
	 * @param num
	 * @param userId
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rechargeDeposit(int num, Integer userId) {
		CoreDepositPurchase currentRefundingCdp = getCurrentRefundingDeposit(userId);
		if (currentRefundingCdp != null) {
			throw new LogicException("当前保证金正在申退中");
		}
		CoreDepositPurchase currentValidCdp = getCurrentValidDeposit(userId);
		if (currentValidCdp == null) {//新添加
			currentValidCdp = new CoreDepositPurchase();
			currentValidCdp.setUsable(true);
			currentValidCdp.setUserId(userId);
			currentValidCdp.setDeposit(num);
		} else {//追加保证金
			currentValidCdp.setDeposit(currentValidCdp.getDeposit() + num);
		}

		if (!this.saveOrUpdate(currentValidCdp)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "充值保证金失败");
		}
		currencyChangeService.addCurrencyChangeRecord(userId, CurrencyType.DEPOSIT, CurrencyChangeReasonType.RECHARGE_CASH_DEPOSIT, num);
		sysUserService.updateUserDeposit(sysUserService.getById(userId), currentValidCdp.getDeposit());
	}

	/**
	 * 保证金退款申请
	 *
	 * @param user
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void refundDeposit(SysUser user) {
		CoreDepositPurchase cdp = getCurrentValidDeposit(user.getId());
		if (cdp == null) {
			throw new LogicException("当前没有可申退的保证金");
		}
		cdp.setRefundTime(LocalDateTime.now());
		cdp.setUsable(false);
		if (!updateById(cdp)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新退还保证金申请出错");
		}
		//保证金退款，扣除1%的手续费
		int fee = cdp.getDeposit() * Integer.parseInt(SystemConfig.WITHDEAWAL_DEPOSIT_FEE.getPropertyValue()) / 100;
		//TODO 保证金盈利的手续费应该在货币变化表中体现为人民币增加
		//提交退款审核申请
		withdrawalService.requestWithdrawal(user.getId(), cdp.getDeposit() - fee, CurrencyChangeReasonType.WITHDRAWAL_CASH_DEPOSIT);
		sysUserService.updateUserDeposit(user, 0);
	}

	@Override
	public void refundDepositFinish(Integer userId) {
		UpdateWrapper<CoreDepositPurchase> uw = new UpdateWrapper<>();
		uw.lambda().eq(CoreDepositPurchase::getUserId, userId)
				.eq(CoreDepositPurchase::getUsable, false)
				.isNotNull(CoreDepositPurchase::getRefundTime)
				.isNull(CoreDepositPurchase::getRefundFinishTime)
				.set(CoreDepositPurchase::getRefundFinishTime, LocalDateTime.now());
		if (!update(uw)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新保证金申退完成时间出错");
		}
	}

	@Override
	public CoreDepositPurchase getCurrentValidDeposit(Integer userId) {
		QueryWrapper<CoreDepositPurchase> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreDepositPurchase::getUserId, userId)
				.eq(CoreDepositPurchase::getUsable, true);
		return getOne(qw);
	}

	/**
	 * 查询当前申退中的保证金
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public CoreDepositPurchase getCurrentRefundingDeposit(Integer userId) {
		QueryWrapper<CoreDepositPurchase> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreDepositPurchase::getUserId, userId)
				.eq(CoreDepositPurchase::getUsable, false)
				.isNull(CoreDepositPurchase::getRefundFinishTime);
		return getOne(qw);
	}
}
