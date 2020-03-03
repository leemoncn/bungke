package com.leemon.wushiwan.enums;

/**
 * @description: 支付类型
 * @author: leemon
 * @create: 2019-10-22 14:35
 **/
public enum PayType {
	None(""),
	RechargeDeposit("充值保证金"),//充值保证金
	RechargeMissionCoin("充值任务币"),//充值任务币
	Partner("充值合作商");//合作商付费

	private String title;

	PayType(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
