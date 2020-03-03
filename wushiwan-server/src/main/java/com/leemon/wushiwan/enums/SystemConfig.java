package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * @description: 系统配置，一般是有数值的
 * @author: leemon
 * @create: 2019-03-27 14:16
 **/
public enum SystemConfig implements PropertyInterface {
	TOP_MISSION_PRICE(55),// 任务置顶收费价格(分/小时)
	WITHDEAWAL_DEPOSIT_FEE(56),// 提现保证金手续费(%)
	MANIFEST(65),// 前端manifest
	IN_REVIEW_APP(75);// 记录哪些APP在审核

	private int typePropertyId;

	SystemConfig(int typePropertyId) {
		this.typePropertyId = typePropertyId;
	}

	/**
	 * 获取这个枚举的类型
	 *
	 * @return
	 */
	@Override
	public PropertyType getPropertyType() {
		return PropertyType.SYSTEM_CONFIG;
	}

	/**
	 * 获取内容
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return getNameById(typePropertyId);
	}

	/**
	 * 获取具体数值，存储在数据库value字段
	 *
	 * @return
	 */
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
