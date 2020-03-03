package com.leemon.wushiwan.pay;

/**
 * @description:
 * @author: leemon
 * @create: 2019-07-09 10:20
 **/
public interface PayService {

	/**
	 * 请求H5支付
	 *
	 * @param tradeNo     系统生成的订单号
	 * @param amount      总价，单位分
	 * @param title       商品标题
	 * @param description 商品描述
	 * @return 前端需要访问的网址
	 */
	String h5Pay(String tradeNo, Integer amount, String title, String description, String passbackStr);

	/**
	 * 请求APP支付
	 *
	 * @param tradeNo     系统生成的订单号
	 * @param amount      总价，单位分
	 * @param title       商品标题
	 * @param description 商品描述
	 * @return 前端需要访问的网址
	 */
	String appPay(String tradeNo, Integer amount, String title, String description, String passbackStr);

	/**
	 * 转账到账户
	 *
	 * @param transferNo      系统生成的单号
	 * @param account         账户（手机号或者邮箱）
	 * @param amount          总价，单位分
	 * @param accountRealName 真实姓名
	 * @param remark          备注
	 * @return 支付宝转账单据号
	 */
	String transferToAccount(String transferNo, String account, Integer amount, String accountRealName, String remark);
}
