package com.leemon.wushiwan.pay.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

/**
 * @description:
 * @author: leemon
 * @create: 2019-07-09 10:43
 **/
public class Alipay {
	private Alipay() {

	}

	public static AlipayClient createAlipayClient() {
		return new DefaultAlipayClient(AlipayPayConfig.GATEWAY_URL, AlipayPayConfig.appId, AlipayPayConfig.appPrivateKey, AlipayPayConfig.FORMAT, AlipayPayConfig.CHARSET, AlipayPayConfig.alipayPublicKey, AlipayPayConfig.SIGN_TYPE);
	}
}
