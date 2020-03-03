package com.leemon.wushiwan.util;

/**
 * @description:
 * @author: leemon
 * @create: 2019-05-10 09:41
 **/
public class Base64Util {
	/**
	 * 根据前端的base64字符串，提取图片的扩展名,
	 *
	 * @param typeStr data:image/png;base64
	 * @return
	 */
	public static String getFileExtensionFromBase64String(String typeStr) {
		return typeStr.split(";")[0].split("/")[1];
	}
}
