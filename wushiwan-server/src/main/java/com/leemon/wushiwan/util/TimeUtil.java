package com.leemon.wushiwan.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @description:
 * @author: leemon
 * @create: 2019-07-27 10:52
 **/
public class TimeUtil {

	public static String formatTime(LocalDateTime ldt) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return ldt.format(formatter);
	}
}
