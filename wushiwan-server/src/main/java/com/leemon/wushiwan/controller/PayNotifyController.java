package com.leemon.wushiwan.controller;

import com.alibaba.fastjson.JSON;
import com.leemon.wushiwan.dto.PayPassbackParam;
import com.leemon.wushiwan.pay.alipay.AlipayCheckService;
import com.leemon.wushiwan.pay.alipay.TradeStatus;
import com.leemon.wushiwan.service.IFinancialTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:回调接收
 * @author: limeng
 * @create: 2019-07-10 21:26
 **/
@RestController
@Slf4j
@RequestMapping("/notify")
public class PayNotifyController {

	private final AlipayCheckService alipayCheckService;
	private final IFinancialTradeService tradeService;

	private static final String ALIPAY_RECEIVE_NOTIFY = "success";

	public PayNotifyController(AlipayCheckService alipayCheckService, IFinancialTradeService tradeService) {
		this.alipayCheckService = alipayCheckService;
		this.tradeService = tradeService;
	}

	private Map<String, String> convertRequestParamsToMap(HttpServletRequest req) {
		Map<String, String[]> params = req.getParameterMap();
		Map<String, String> m = new HashMap<>(16);
		params.forEach((k, v) -> {
			if (v.length == 1) {
				m.put(k, v[0]);
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < v.length; i++) {
					builder.append(v[i]);
					if (i != v.length - 1) {
						builder.append(",");
					}
				}
				m.put(k, builder.toString());
			}
		});
		return m;
	}

	private String convertMapToString(Map<String, String> map) {
		StringBuilder buileder = new StringBuilder();
		map.forEach((s, s2) -> {
			if (buileder.length() == 0) {
				buileder.append(String.format("%s=%s", s, s2));
			} else {
				buileder.append(String.format("&%s=%s", s, s2));
			}
		});
		return buileder.toString();
	}

	@RequestMapping(value = "/alipay", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public String alipayPayNotify(HttpServletRequest request, HttpServletResponse response) {
		// 同时需要校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		// 现在以商户ID为维度，不需要校验seller_id
		try {
			Map<String, String> map = convertRequestParamsToMap(request);
			log.info("收到支付宝回调，map = {}", convertMapToString(map));
			if (map.size() == 0) {
				return "no data";
			}
			String status = map.get("trade_status");
			if (!status.equals(TradeStatus.TRADE_SUCCESS.name()) && !status.equals(TradeStatus.TRADE_FINISHED.name())) {
				return ALIPAY_RECEIVE_NOTIFY;
			}
			String decodePassback = URLDecoder.decode(map.get("passback_params"), StandardCharsets.UTF_8.name());
			String attach = new String(Base64.getDecoder().decode(decodePassback));
			log.info("支付宝回调，passback_params = {}", attach);

			alipayCheckService.checkSign(map);
			//商户订单号
			String outTradeNo = map.get("out_trade_no");
			BigDecimal totalAmount = new BigDecimal(map.get("total_amount"));
			int amount = totalAmount.multiply(new BigDecimal("100.0")).intValue();
//			//支付宝订单号
			String thirdTradeNo = map.get("trade_no");

			PayPassbackParam param = JSON.parseObject(attach, PayPassbackParam.class);
			tradeService.finishTrade(outTradeNo, thirdTradeNo, amount, param);
		} catch (Exception e) {
			log.error("接收支付宝通知处理逻辑出错,{}", e.getMessage(), e);
		}
		return ALIPAY_RECEIVE_NOTIFY;
	}
}
