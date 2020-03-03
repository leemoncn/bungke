package com.leemon.wushiwan.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author: leemon
 * @create: 2019-07-09 11:12
 **/
@Service
public class AlipayCheckService {
	public void checkSign(Map<String, String> m) {
		boolean signCheck;
		try {
			signCheck = AlipaySignature.rsaCheckV1(m, AlipayPayConfig.alipayPublicKey, AlipayPayConfig.CHARSET, AlipayPayConfig.SIGN_TYPE);
		} catch (AlipayApiException e) {
			throw new LogicException(e.getMessage());
		}
		if (!signCheck) {
			throw new LogicException(ErrorCode.PAY_ERROR, "支付宝签名校验错误");
		}
	}

	public void checkResult(AlipayResponse res) {
		//支付宝同步返回的回自动验签，异步返回的需要手动验签
		if (res == null) {
			throw new LogicException(ErrorCode.PAY_ERROR, "支付宝返回的数据为空");
		}
		boolean result = res.isSuccess();
		if (!result) {
			String code = res.getCode();
			String msg = res.getMsg();
			String subCode = res.getSubCode();
			String subMsg = res.getSubMsg();
			String logMsg = String.format("支付宝验证返回值失败，code = %s, msg = %s, subCode = %s, subMsg = %s", code, msg, subCode, subMsg);
			if ("20000".equals(code) || //服务不可用
					"20001".equals(code)) {//授权权限不足
				throw new LogicException(ErrorCode.PAY_ERROR, logMsg);
			} else if ("40001".equals(code) || //缺少必选参数
					"40002".equals(code) || // 非法的参数
					"ACQ.INVALID_PARAMETER".equals(subCode.toUpperCase()) || // 参数无效
					"ACQ.PARTNER_ERROR".equals(subCode.toUpperCase())) { // 应用APP_ID填写错误
				throw new LogicException(ErrorCode.PAY_ERROR, logMsg);
			} else if ("ACQ.TRADE_HAS_SUCCESS".equals(subCode.toUpperCase())) {//交易已支付
				throw new LogicException(ErrorCode.PAY_ERROR, logMsg);
			} else if ("ACQ.TRADE_HAS_CLOSE".equals(subCode.toUpperCase())) {//交易已关闭
				throw new LogicException(ErrorCode.PAY_ERROR, logMsg);
			} else if ("ACQ.SELLER_BALANCE_NOT_ENOUGH".equals(subCode.toUpperCase())) {//退款账户余额不足
				throw new LogicException(ErrorCode.PAY_ERROR, logMsg);
			} else {
				throw new LogicException(ErrorCode.PAY_ERROR, logMsg);
			}
		}
	}
}
