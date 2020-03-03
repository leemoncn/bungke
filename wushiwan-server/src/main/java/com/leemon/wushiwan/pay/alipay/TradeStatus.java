package com.leemon.wushiwan.pay.alipay;

/**
 * @description:
 * @author: limeng
 * @create: 2019-07-10 21:58
 **/
public enum TradeStatus {
	/**
	 * 交易支付成功
	 */
	TRADE_SUCCESS,
	/**
	 * 交易结束，不可退款
	 */
	TRADE_FINISHED,
	/**
	 * 交易创建，等待买家付款
	 */
	WAIT_BUYER_PAY,
	/**
	 * 未付款交易超时关闭，或支付完成后全额退款
	 */
	TRADE_CLOSED,
}
