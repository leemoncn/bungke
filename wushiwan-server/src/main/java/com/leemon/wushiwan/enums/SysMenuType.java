package com.leemon.wushiwan.enums;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @description: 菜单的类型
 * @author: leemon
 * @create: 2019-04-18 10:23
 **/
public enum SysMenuType implements IEnum<Integer>, JSONSerializable {
	ROOT_MENU,//0:一级菜单
	CHILD_MENU,//1:子菜单
	BUTTON;//2:按钮权限

	/**
	 * 枚举数据库存储值
	 */
	@Override
	public Integer getValue() {
		return ordinal();
	}

	@Override
	public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
		serializer.write(getValue());
	}
}
