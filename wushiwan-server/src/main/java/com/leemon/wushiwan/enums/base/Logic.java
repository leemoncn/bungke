package com.leemon.wushiwan.enums.base;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * @description: 逻辑删除
 * @author: limeng
 * @create: 2019-03-26 22:30
 **/
@Getter
public enum Logic implements IEnum<Integer> {
	NOT_DELETE(0),//未删除
	DELETED(1);//已删除

	private final int deleteFlag;

	Logic(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	@Override
	public Integer getValue() {
		return deleteFlag;
	}
}
