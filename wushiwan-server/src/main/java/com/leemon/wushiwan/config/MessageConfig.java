package com.leemon.wushiwan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: 发短信配置
 * @author: limeng
 * @create: 2019-06-20 20:15
 **/
@Component
@ConfigurationProperties(prefix = "message")
public class MessageConfig {
	// 短信应用SDK AppID
	private int appid; // 1400开头
	// 短信应用SDK AppKey
	private String appkey;
	// 短信模板ID，需要在短信应用中申请
	private int templateId; // NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
	// 签名
	private String smsSign; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`

	private int validMinute;

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public String getSmsSign() {
		return smsSign;
	}

	public void setSmsSign(String smsSign) {
		this.smsSign = smsSign;
	}

	public int getValidMinute() {
		return validMinute;
	}

	public void setValidMinute(int validMinute) {
		this.validMinute = validMinute;
	}
}
