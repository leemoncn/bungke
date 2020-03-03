package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.FinancialWithdrawal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.system.entity.SysUser;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-06-29
 */
public interface IFinancialWithdrawalService extends IService<FinancialWithdrawal> {

	/**
	 * 申请提现
	 *
	 * @param userId
	 * @param money            转换成人民币后的数值（要扣除给上级分成什么的）,单位分
	 * @param reasonPropertyId 提现原因
	 */
	void requestWithdrawal(Integer userId, Integer money, CurrencyChangeReasonType reasonPropertyId);

	void approveWithdrawal(Integer withdrawalId, Boolean result, SysUser approveUser);
}
