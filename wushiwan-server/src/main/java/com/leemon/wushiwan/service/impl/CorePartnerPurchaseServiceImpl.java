package com.leemon.wushiwan.service.impl;

import com.google.common.base.Strings;
import com.leemon.wushiwan.dto.PartnerInfo;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.entity.CorePartnerPurchase;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.PartnerTimeType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CorePartnerPurchaseMapper;
import com.leemon.wushiwan.service.ICorePartnerPurchaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.service.IFinancialWithdrawalService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-06-27
 */
@Service
public class CorePartnerPurchaseServiceImpl extends ServiceImpl<CorePartnerPurchaseMapper, CorePartnerPurchase> implements ICorePartnerPurchaseService {

	@Autowired
	private ISysUserService sysUserService;
	private final CorePartnerPurchaseMapper corePartnerPurchaseMapper;
	private final ICorePartnerService corePartnerService;
	private final IFinancialWithdrawalService withdrawalService;


	public CorePartnerPurchaseServiceImpl(CorePartnerPurchaseMapper corePartnerPurchaseMapper, ICorePartnerService corePartnerService, IFinancialWithdrawalService withdrawalService) {
		this.corePartnerPurchaseMapper = corePartnerPurchaseMapper;
		this.corePartnerService = corePartnerService;
		this.withdrawalService = withdrawalService;
	}

	/**
	 * 给某个用户添加合作商,在调用这个方法之前，添加流水记录
	 *
	 * @param sysUser
	 * @param partnerId
	 * @param operator  操作者，如果有操作者说明是后台添加的，不收费
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addNewPartner(SysUser sysUser, Integer partnerId, PartnerTimeType timeType, String operator) {
		if (timeType == PartnerTimeType.NONE) {
			throw new LogicException(ErrorCode.SYS_ERROR, "不存在的合作商时间类型");
		}
		//先看看用户当前是否是合作商
		PartnerInfo userCurrentPartner = corePartnerPurchaseMapper.selectValidPartnerInfoByUser(sysUser.getId());
		//想要购买的合作商
		CorePartner buyPartner = corePartnerService.getById(partnerId);
		if (buyPartner == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID);
		}
		if (buyPartner.getLevel() == 0) {
			throw new LogicException(ErrorCode.SYS_ERROR, "普通合作商无需购买");
		}
		if (userCurrentPartner != null) {//当前是合作商
			if (buyPartner.getLevel() < userCurrentPartner.getLevel()) {
				throw new LogicException("合作商只可升级，不能降级");
			}
			//先将之前的合作商计划禁用
			disablePartner(userCurrentPartner.getId());
		}
		//购买高级合作商，或者续费之前的合作商
		//更新用户
		sysUserService.updateUserPartnerId(sysUser, partnerId);
		//添加新的合作商购买记录
		CorePartnerPurchase cpp = new CorePartnerPurchase();
		cpp.setUserId(sysUser.getId());
		cpp.setPartnerId(partnerId);
		cpp.setTimeType(timeType);
		LocalDate now = LocalDate.now();
		cpp.setStartTime(now);
		cpp.setUpdater(operator);

		if (userCurrentPartner != null && buyPartner.getLevel().equals(userCurrentPartner.getLevel())) {//之前的合作商续费
			if (timeType == PartnerTimeType.MONTH) {
				cpp.setEndTime(userCurrentPartner.getEndTime().plusMonths(1));
				cpp.setPrice(buyPartner.getMouthPrice());
			} else {
				cpp.setEndTime(userCurrentPartner.getEndTime().plusYears(1));
				cpp.setPrice(buyPartner.getYearPrice());
			}
		} else {//购买新的高等级合作商
			if (timeType == PartnerTimeType.MONTH) {
				cpp.setPrice(buyPartner.getMouthPrice());
				cpp.setEndTime(now.plusMonths(1));
			} else {
				cpp.setPrice(buyPartner.getYearPrice());
				cpp.setEndTime(now.plusYears(1));
			}
		}
		cpp.setUsable(true);
		if (!Strings.isNullOrEmpty(operator)) {
			cpp.setPrice(0);
		}
		if (!save(cpp)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "保存合作商出错，id = %d,sysUser = %s,timeType = %d", partnerId, sysUser.toString(), timeType.ordinal());
		}
	}

//	/**
//	 * 对已存在的合作商计划进行退款
//	 *
//	 * @param currentPartner 退款金额
//	 * @return
//	 */
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public int refundPartner(@NotNull PartnerInfo currentPartner) {
//		int refundMoney = getOldPartnerRefundMoney(currentPartner);
//		if (refundMoney > 0) {
//			//添加提现申请
//			withdrawalService.requestWithdrawal(currentPartner.getUserId(), refundMoney, CurrencyChangeReasonType.OLD_PARTNER_REFUND);
//		}
//		disablePartner(currentPartner.getId());
//		return refundMoney;
//	}

	private int getOldPartnerRefundMoney(@NotNull PartnerInfo currentPartner) {
		if (currentPartner.getLevel() == 0) {
			return 0;
		}
		LocalDate startTime = currentPartner.getStartTime();
		LocalDate endTime = currentPartner.getEndTime();
		int remainDays = (int) Duration.between(LocalDate.now().plusDays(1).atStartOfDay(), endTime.atStartOfDay()).toDays();
		if (remainDays <= 0) {//剩余时间不足，无需退款
			return 0;
		}
		//合约总计时间
		int totalDays = (int) Duration.between(startTime.atStartOfDay(), endTime.atStartOfDay()).toDays();
		//单位分
		return (int) ((float) remainDays / (float) totalDays * currentPartner.getPrice());
	}

	@Override
	public void disablePartner(@NotNull Integer corePartnerPurchaseId) {
		if (corePartnerPurchaseId == null) {
			throw new LogicException(ErrorCode.SYS_ERROR, "合作商ID为null");
		}
		CorePartnerPurchase cpp = new CorePartnerPurchase();
		cpp.setId(corePartnerPurchaseId);
		cpp.setUsable(false);
		if (!updateById(cpp)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "禁用合作商出错，id = %s", corePartnerPurchaseId);
		}
	}

	@Override
	public List<PartnerInfo> getValidPartnerInfoList() {
		return corePartnerPurchaseMapper.selectValidPartnerInfoList();
	}
}
