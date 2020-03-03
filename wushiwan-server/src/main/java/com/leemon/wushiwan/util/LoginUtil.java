package com.leemon.wushiwan.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @description:
 * @author: limeng
 * @create: 2019-11-03 11:23
 **/
public class LoginUtil {

	public static String getNicknameFromDataForWechat(String data) {
		JSONObject json = (JSONObject) JSONObject.parse(data);
		return json.getJSONObject("userInfo").getString("nickname");
	}

	public static String getHeadImgUrlFromDataForWechat(String data) {
		JSONObject json = (JSONObject) JSONObject.parse(data);
		return json.getJSONObject("userInfo").getString("headimgurl");
	}
}
