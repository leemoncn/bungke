package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.FinancialTrade;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.dto.PayPassbackParam;
import com.leemon.wushiwan.enums.PayType;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-07-14
 */
public interface IFinancialTradeService extends IService<FinancialTrade> {

	/**
	 * 发起一笔支付
	 *
	 * @param userId
	 * @param amount
	 * @param payType
	 * @param data
	 * @return APP支付的加密串或者h5的form，取决于header的isApp字段
	 */
	String startPay(Integer userId, Integer amount, PayType payType, String data, boolean isApp);

	/**
	 * 收到充值完成的回调,会自动添加一条流水
	 *
	 * @param tradeNo
	 * @param amount       单位分
	 * @param thirdTradeNo
	 */
	void finishTrade(String tradeNo, String thirdTradeNo, Integer amount, PayPassbackParam param);
}
