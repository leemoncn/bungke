package com.leemon.wushiwan.pay.alipay;

import lombok.Data;

/**
 * @description:
 * @author: leemon
 * @create: 2019-07-09 10:45
 **/
public class AlipayPayConfig {
	public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
	//参数返回格式，只支持json
	public static final String FORMAT = "json";
	//请求和签名使用的字符编码格式，支持GBK和UTF-8
	public static final String CHARSET = "UTF-8";
	//商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
	public static final String SIGN_TYPE = "RSA2";

	public static String appId = "";
	//开发者应用私钥，由开发者自己生成
	public static String appPrivateKey = "";
	//支付宝公钥，由支付宝生成
	public static String alipayPublicKey = "";
}
