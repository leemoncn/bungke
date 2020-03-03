package com.leemon.wushiwan.enums;

/**
 * @description: 聊天的状态类型
 * @author: leemon
 * @create: 2019-10-22 14:35
 **/
public enum ChatStatusType {
	None(""),
	CommitMission("提交审核"),//提交审核
	MissionReviewSuccess("合格"),//商家审核合格
	MissionReviewFailed("不合格"),
	Complain("提交申诉"),//用户提交申诉
	ComplainSuccess("终审合格"),//官方终审
	ComplainFailed("终审不合格");

	private String title;

	ChatStatusType(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
