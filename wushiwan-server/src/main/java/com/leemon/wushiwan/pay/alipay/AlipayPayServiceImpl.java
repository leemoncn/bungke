package com.leemon.wushiwan.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.pay.PayService;
import com.leemon.wushiwan.util.RMBUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @description:
 * @author: leemon
 * @create: 2019-07-09 10:33
 **/
@Service
@Slf4j
public class AlipayPayServiceImpl implements PayService {

	private final AlipayCheckService checkService;
	private final Config config;

	public AlipayPayServiceImpl(AlipayCheckService checkService, Config config) {
		this.checkService = checkService;
		this.config = config;
	}

	/**
	 * 请求H5支付
	 *
	 * @param tradeNo     系统生成的订单号
	 * @param amount      总价，单位分
	 * @param title       商品标题
	 * @param description 商品描述
	 * @return 前端需要访问的form
	 */
	@Override
	public String h5Pay(String tradeNo, Integer amount, String title, String description, String passbackStr) {
		AlipayClient alipayClient = Alipay.createAlipayClient();
		AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
		alipayRequest.setReturnUrl(config.getClientDomain() + "/recharge-finish");//前端回调地址
		alipayRequest.setNotifyUrl(config.getPayNotifyUrl());//后端通知地址
		try {
			alipayRequest.setBizContent(String.format("{" +
					" \"out_trade_no\":\"%s\"," +
					" \"total_amount\":\"%s\"," +
					" \"subject\":\"%s\"," +
					"    \"body\":\"%s\"," +
					" \"product_code\":\"QUICK_WAP_PAY\"," +
					" \"passback_params\":\"%s\"" +
					" }", tradeNo, RMBUtil.fenToYuan(amount) + "", title, description, URLEncoder.encode(Base64.getEncoder().encodeToString(passbackStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8.name())));//填充业务参数
		} catch (UnsupportedEncodingException e) {
			throw new LogicException(ErrorCode.PAY_ERROR, e.getMessage());
		}
		AlipayTradeWapPayResponse response;
		try {
			response = alipayClient.pageExecute(alipayRequest);
		} catch (AlipayApiException e) {
			throw new LogicException(ErrorCode.PAY_ERROR, e.getMessage());
		}
		log.info("支付宝H5请求，req = {}，res = {}", alipayRequest.getBizContent(), response.getBody());
		checkService.checkResult(response);
		String form = response.getBody();
		if (Strings.isNullOrEmpty(form)) {
			throw new LogicException(ErrorCode.PAY_ERROR, "支付宝返回form是空");
		}
		return form;
	}

	/**
	 * 请求APP支付
	 *
	 * @param tradeNo     系统生成的订单号
	 * @param amount      总价，单位分
	 * @param title       商品标题
	 * @param description 商品描述
	 * @param passbackStr
	 * @return 前端需要访问的网址
	 */
	@Override
	public String appPay(String tradeNo, Integer amount, String title, String description, String passbackStr) {
		AlipayClient alipayClient = Alipay.createAlipayClient();
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		request.setNotifyUrl(config.getPayNotifyUrl());//后端通知地址
		try {
			request.setBizContent(String.format("{" +
					" \"out_trade_no\":\"%s\"," +
					" \"total_amount\":\"%s\"," +
					" \"subject\":\"%s\"," +
					"    \"body\":\"%s\"," +
					" \"passback_params\":\"%s\"" +
					" }", tradeNo, RMBUtil.fenToYuan(amount) + "", title, description, URLEncoder.encode(Base64.getEncoder().encodeToString(passbackStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8.name())));//填充业务参数
		} catch (UnsupportedEncodingException e) {
			throw new LogicException(ErrorCode.PAY_ERROR, e.getMessage());
		}
		AlipayTradeAppPayResponse response = null;
		try {
			response = alipayClient.sdkExecute(request);
		} catch (AlipayApiException e) {
			throw new LogicException(ErrorCode.PAY_ERROR, e.getMessage());
		}
		log.info("支付宝APP请求，req = {}，res = {}", request.getBizContent(), response.getBody());
		checkService.checkResult(response);

		String body = response.getBody();
		if (Strings.isNullOrEmpty(body)) {
			throw new LogicException(ErrorCode.PAY_ERROR, "支付宝返回body是空");
		}
		return body;
	}

	/**
	 * 转账到账户
	 *
	 * @param transferNo      系统生成的单号(最长64位)
	 * @param account         账户（手机号或者邮箱）
	 * @param amount          总价，单位分
	 * @param accountRealName 真实姓名
	 * @param remark          备注
	 * @return 支付宝转账单据号
	 */
	@Override
	public String transferToAccount(String transferNo, String account, Integer amount, String accountRealName, String remark) {
		AlipayClient alipayClient = Alipay.createAlipayClient();
		AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
		request.setBizContent(String.format("{" +
				"\"out_biz_no\":\"%s\"," +
				"\"payee_type\":\"ALIPAY_LOGONID\"," +
				"\"payee_account\":\"%s\"," +
				"\"amount\":\"%s\"," +
				"\"payer_show_name\":\"%s\"," +
				"\"payee_real_name\":\"%s\"," +
				"\"remark\":\"%s\"" +
				"  }", transferNo, account, RMBUtil.fenToYuan(amount) + "", config.getAlipayTransferName(), accountRealName, remark));
		AlipayFundTransToaccountTransferResponse response = null;
		try {
			response = alipayClient.execute(request);
		} catch (AlipayApiException e) {
			throw new LogicException(ErrorCode.PAY_ERROR, e.getMessage());
		}
		log.info("支付宝转账到账户，req = {}，res = {}", request.getBizContent(), response.getBody());
		checkService.checkResult(response);
		return response.getOrderId();
	}
}
