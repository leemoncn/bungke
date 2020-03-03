package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreDepositPurchase;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.system.entity.SysUser;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-07-01
 */
public interface ICoreDepositPurchaseService extends IService<CoreDepositPurchase> {
	/**
	 * 充值保证金
	 *
	 * @param num
	 */
	void rechargeDeposit(int num, Integer userId);

	/**
	 * 申请保证金退款
	 *
	 * @param user
	 */
	void refundDeposit(SysUser user);

	void refundDepositFinish(Integer userId);

	/**
	 * 获取当前可用的保证金信息
	 *
	 * @param userId
	 * @return
	 */
	CoreDepositPurchase getCurrentValidDeposit(Integer userId);

	/**
	 * 查询当前申退中的保证金
	 *
	 * @param userId
	 * @return
	 */
	CoreDepositPurchase getCurrentRefundingDeposit(Integer userId);
}
