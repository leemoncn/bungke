package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * @description: 任务发布情况（对任务发布者来说）
 * @author: leemon
 * @create: 2019-05-28 10:42
 **/
public enum MissionPublishType implements PropertyInterface {
	REVIEWING(28),//审核中
	PUBLISHED(32),//任务已发布
	BAN(29),//任务被封
	OVER_DEADLINE(30),//超过任务截止时间
	FINISH(31),//任务数量全部完成
	REVIEW_REJECT(57),//审核驳回
	OFF(58);//已下架

	private int typePropertyId;

	MissionPublishType(int typePropertyId) {
		this.typePropertyId = typePropertyId;
	}

	@Override
	public PropertyType getPropertyType() {
		return PropertyType.MISSION_PUBLISH;
	}

	@Override
	public String getName() {
		return getNameById(typePropertyId);
	}

	@Override
	public String getPropertyValue() {
		return getValueById(typePropertyId);
	}

	@Override
	public Integer getValue() {
		return typePropertyId;
	}
}
