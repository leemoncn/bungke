package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * @description: 发送通知的类型
 * @author: leemon
 * @create: 2019-03-27 14:16
 **/
public enum NoticeType implements PropertyInterface {
	RESULT_OF_PUBLISH_MISSION(36),// 发布任务审核结果（管理员审核）
	RESULT_OF_FINISH_MISSION(37),// 完成任务审核结果（发布者审核）
	PARTNER_EXPIRED(47),// 合作商过期
	WITHDRAWAL_REVIEW_FINISH(48),// 提现审核完成（由管理员审核，成功或失败）
	WITHDRAWAL_REQUEST(49),// 提交提现申请
	TOP_MISSION(64),//任务置顶成功
	MISSION_OVER_DEADLINE(68),// 任务超过截止日期自动退回任务币
	RECEIVE_CHAT(69),// 任务超过截止日期自动退回任务币
	COMMIT_MISSION(70),// 接任务者完成任务提交
	FROM_ADMIN(71);// 系统后台发送消息

	private int typePropertyId;

	NoticeType(int typePropertyId) {
		this.typePropertyId = typePropertyId;
	}

	/**
	 * 获取这个枚举的类型
	 *
	 * @return
	 */
	@Override
	public PropertyType getPropertyType() {
		return PropertyType.NOTICE;
	}

	/**
	 * 获取内容
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return getNameById(typePropertyId);
	}

	@Override
	public String getPropertyValue() {
		return getValueById(typePropertyId);
	}

	/**
	 * 枚举数据库存储值
	 */
	@Override
	public Integer getValue() {
		return typePropertyId;
	}
}
