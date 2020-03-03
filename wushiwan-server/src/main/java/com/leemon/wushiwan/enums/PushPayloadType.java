package com.leemon.wushiwan.enums;

/**
 * @description: 推送消息类型
 * @author: limeng
 * @create: 2019-12-12 22:59
 **/
public enum PushPayloadType {
	NONE,
	TOAST,//在线显示一个toast，离线显示通知
	NOTICE//完成任务或者从后台推送的通知，在线会在消息页面多一条消息（通过socialnotice触发），消息会显示角标，离线显示通知
}
