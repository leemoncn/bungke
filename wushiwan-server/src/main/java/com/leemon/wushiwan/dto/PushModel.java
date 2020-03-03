package com.leemon.wushiwan.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 个推推送的结构
 * @author: limeng
 * @create: 2019-12-12 22:28
 **/
@Data
@Builder
public class PushModel {
	//通知栏显示的标题
	private String title;
	//通知栏显示的内容
	private String content;
	//通知栏的透传内容
	private List<PushModelPayload> payload;

	public void addSinglePayload(PushModelPayload p) {
		if (payload == null) {
			payload = new ArrayList<>();
		}
		payload.add(p);
	}
}
