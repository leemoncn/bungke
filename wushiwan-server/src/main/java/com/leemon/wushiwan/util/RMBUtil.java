package com.leemon.wushiwan.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @description: 与价格相关的工具类
 * @author: limeng
 * @create: 2019-07-24 20:56
 **/
@Slf4j
public class RMBUtil {

	private static DecimalFormat df = new DecimalFormat("#.00");

	static {
		//全部进位
		df.setRoundingMode(RoundingMode.CEILING);
	}

	/**
	 * 对double进行进位，比如1.1返回2，1.01也返回2，小数点后面只要不是0，就加1
	 *
	 * @param price
	 * @return
	 */
	public static int getIntPrice(double price) {
		return (int) Math.ceil(price);
	}

	public static int getIntPrice(BigDecimal price) {
		return (int) Math.ceil(price.doubleValue());
	}

	/**
	 * 分转化成元
	 *
	 * @return
	 */
	public static double fenToYuan(int price) {
		return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(100.0f)).doubleValue();
	}

	/**
	 * 对double进行进位，保留2位小数，只要2位小数后面有值，全部进位
	 *
	 * @param price
	 * @return
	 */
	public static double getDoublePrice_2(double price) {
		return Double.parseDouble(df.format(price));
	}
}
