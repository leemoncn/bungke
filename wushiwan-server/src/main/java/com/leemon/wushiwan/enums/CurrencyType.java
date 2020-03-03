package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * @description: 货币类型
 * @author: limeng
 * @create: 2019-06-16 21:31
 **/
public enum CurrencyType implements PropertyInterface {
	MISSION_COIN(33),//任务币
	EARNING(34),//收入
	RMB(41),//人民币,充值和提现时会出现人民币流水记录
	DEPOSIT(66);//保证金

	private int typePropertyId;

	CurrencyType(int typePropertyId) {
		this.typePropertyId = typePropertyId;
	}

	@Override
	public PropertyType getPropertyType() {
		return PropertyType.CURRENCY;
	}

	@Override
	public String getName() {
		return getNameById(typePropertyId);
	}

	@Override
	public String getPropertyValue() {
		return getValueById(typePropertyId);
	}

	@Override
	public Integer getValue() {
		return typePropertyId;
	}
}
