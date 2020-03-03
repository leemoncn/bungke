package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * @description: 货币类型
 * @author: limeng
 * @create: 2019-06-16 21:31
 **/
public enum CurrencyChangeReasonType implements PropertyInterface {
	FINISH_MISSION(38),//完成任务,获得收入
	BE_PARTNER(39),//充值合作商
	PUBLISH_MISSION(40),//发布任务，消耗任务币
	RECHARGE_MISSION_COIN(42),//充值任务币
	RECHARGE_CASH_DEPOSIT(43),//充值保证金
	WITHDRAWAL_EARNING(44),//收入提现
	//	OLD_PARTNER_REFUND(45),//升级合作商，旧的合作商退款
	WITHDRAWAL_MISSION_COIN(46),//任务币提现
	EARNING_BY_SUBORDINATE_RECHARGE_MISSION_COIN(50),//下级充值任务币，上级得收入
	WITHDRAWAL_MISSION_COIN_FEE(51),//任务币提现扣除的手续费
	WITHDRAWAL_EARNING_FEE(52),//收入提现扣除的手续费
	EARNING_BY_SUBORDINATE_WITHDRAWAL_EARNING(53),//下级提现收入，上级得手续费分成收入
	WITHDRAWAL_CASH_DEPOSIT(54),//保证金申退申请
	PUBLISH_MISSION_REVIEW_REJECT(59),//发布任务审核被拒，退还任务币
	PUBLISHED_MISSION_ADD_COUNT(60),//已发布的任务追加数量
	PUBLISHED_MISSION_CHANGE_PRICE(61),//已发布的任务上调价格
	OFF_MISSION(62),//已发布的任务下架
	TOP_MISSION(63),//任务置顶
	PUBLISH_MISSION_OVER_DEADLINE(67),//发布任务超过截止日期，退还没做完的任务币
	WITHDRAWAL_EARNING_REJECT(72),//收入提现审核不通过
	WITHDRAWAL_MISSION_COIN_REJECT(73),//任务币提现审核不通过
	WITHDRAWAL_CASH_DEPOSIT_REJECT(74);//保证金提现审核不通过

	private int typePropertyId;

	CurrencyChangeReasonType(int typePropertyId) {
		this.typePropertyId = typePropertyId;
	}

	@Override
	public PropertyType getPropertyType() {
		return PropertyType.CURRENCY_CHANGE_REASON;
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
