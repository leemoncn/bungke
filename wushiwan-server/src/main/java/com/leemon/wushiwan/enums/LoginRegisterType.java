package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 登陆类型
 * @author: limeng
 * @create: 2019-03-24 20:46
 **/
@Slf4j
public enum LoginRegisterType implements PropertyInterface {
	USERNAME_PASSWORD(1),
	WECHAT(2),
	MOBILE(35);//手机号登录

	private int typePropertyId;

	LoginRegisterType(int typePropertyId) {
		this.typePropertyId = typePropertyId;
	}

	@Override
	public PropertyType getPropertyType() {
		return PropertyType.LOGIN_REGISTER;
	}

	@Override
	public String getName() {
		return getNameById(typePropertyId);
	}

	@Override
	public String getPropertyValue() {
		return getValueById(typePropertyId);
	}

	/**
	 * 枚举数据库存储值
	 */
	@Override
	public Integer getValue() {
		return typePropertyId;
	}
}

