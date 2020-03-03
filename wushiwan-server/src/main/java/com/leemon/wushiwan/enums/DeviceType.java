package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * @description: 登录状态，是否允许登录
 * @author: leemon
 * @create: 2019-03-27 14:16
 **/
public enum DeviceType implements PropertyInterface {
	ALL(18),// 全部
	ANDROID(19),// 安卓
	APPLE(20);// 苹果

	private int deviceTypePropertyId;

	DeviceType(int deviceTypePropertyId) {
		this.deviceTypePropertyId = deviceTypePropertyId;
	}

	/**
	 * 获取这个枚举的类型
	 *
	 * @return
	 */
	@Override
	public PropertyType getPropertyType() {
		return PropertyType.DEVICE_TYPE;
	}

	/**
	 * 获取内容
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return getNameById(deviceTypePropertyId);
	}

	@Override
	public String getPropertyValue() {
		return getValueById(deviceTypePropertyId);
	}

	/**
	 * 枚举数据库存储值
	 */
	@Override
	public Integer getValue() {
		return deviceTypePropertyId;
	}
}
