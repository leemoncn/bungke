package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * @description: 登录状态，是否允许登录
 * @author: leemon
 * @create: 2019-03-27 14:16
 **/
public enum LoginStatus implements PropertyInterface {
	ALLOW(3),// 允许登录
	DENIED(4);// 禁止登录

	private int loginStatusPropertyId;

	LoginStatus(int loginStatusPropertyId) {
		this.loginStatusPropertyId = loginStatusPropertyId;
	}

	/**
	 * 获取这个枚举的类型
	 *
	 * @return
	 */
	@Override
	public PropertyType getPropertyType() {
		return PropertyType.LOGIN_STATUS;
	}

	/**
	 * 获取内容
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return getNameById(loginStatusPropertyId);
	}

	@Override
	public String getPropertyValue() {
		return getValueById(loginStatusPropertyId);
	}

	/**
	 * 枚举数据库存储值
	 */
	@Override
	public Integer getValue() {
		return loginStatusPropertyId;
	}
}
