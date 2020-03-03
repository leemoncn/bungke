package com.leemon.wushiwan.enums;

import com.leemon.wushiwan.enums.base.PropertyInterface;
import com.leemon.wushiwan.enums.base.PropertyType;
import org.omg.CORBA.TIMEOUT;

/**
 * @description: 任务执行情况（对任务接受者来说）
 * @author: limeng
 * @create: 2019-05-19 19:13
 **/
public enum MissionProceedType implements PropertyInterface {
	WAIT_COMMIT(21),//待提交
	IN_REVIEW(22),//审核中
	REJECTED(23),//不合格
	FINISHED(24),//已完成
	TIME_OUT(25),//已超时
	OVER_DEADLINE(26),//任务时间已截止
	BAN(27);//任务被封,如果任务被封，已经接取了任务的人，状态会变成任务完成并获得收入

	private int typePropertyId;

	MissionProceedType(int typePropertyId) {
		this.typePropertyId = typePropertyId;
	}

	@Override
	public PropertyType getPropertyType() {
		return PropertyType.MISSION_PROCEED;
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
