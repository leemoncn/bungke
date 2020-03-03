package com.leemon.wushiwan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.base.Splitter;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.vo.SysUserDetail;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ISysUserService extends IService<SysUser> {

	/**
	 * 更新用户任务币
	 *
	 * @param user   更新谁
	 * @param change 数量，可以正数或者负数
	 */
	void updateUserMissionCoin(SysUser user, CurrencyChangeReasonType type, int change);

	/**
	 * 更新用户收入
	 *
	 * @param user   更新谁
	 * @param change 数量，可以正数或者负数
	 */
	void updateUserEarning(SysUser user, CurrencyChangeReasonType type, int change);

	/**
	 * 更新用户的合作商ID
	 *
	 * @param user
	 * @param partnerId
	 */
	void updateUserPartnerId(SysUser user, int partnerId);

	/**
	 * 更新用户保证金
	 *
	 * @param user
	 * @param deposit
	 */
	void updateUserDeposit(SysUser user, int deposit);

	/**
	 * 更新用户支付宝账号和真实姓名
	 *
	 * @param user
	 * @param account
	 * @param realName
	 */
	void updateUserAlipayAccountAndRealName(SysUser user, String account, String realName);

	SysUserDetail getSysUserDetailById(Serializable id);

	void removePushIdFromAllUser(String pushId);
}
