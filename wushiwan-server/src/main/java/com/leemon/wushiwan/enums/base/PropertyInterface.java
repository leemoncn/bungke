package com.leemon.wushiwan.enums.base;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.leemon.wushiwan.entity.SysProperty;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.service.ISysPropertyService;
import com.leemon.wushiwan.util.SpringUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @description:
 * @author: limeng
 * @create: 2019-03-24 21:03
 **/
public interface PropertyInterface extends IEnum<Integer>, JSONSerializable {
	/**
	 * 获取这个枚举的类型
	 *
	 * @return
	 */
	PropertyType getPropertyType();

	/**
	 * 获取内容
	 *
	 * @return
	 */
	String getName();

	String getPropertyValue();

	default String getNameById(Integer id) {
		ISysPropertyService sysPropertyService = SpringUtil.getBean(ISysPropertyService.class);
		Optional<SysProperty> o = Optional.ofNullable(sysPropertyService.getById(id));
		return o.map(SysProperty::getName).orElseThrow(() -> new LogicException(ErrorCode.SYS_ERROR));
	}

	default String getValueById(Integer id) {
		ISysPropertyService sysPropertyService = SpringUtil.getBean(ISysPropertyService.class);
		Optional<SysProperty> o = Optional.ofNullable(sysPropertyService.getById(id));
		return o.map(SysProperty::getValue).orElseThrow(() -> new LogicException(ErrorCode.SYS_ERROR));
	}

	default String getString() {
		return getValue().toString();
	}

	@Override
	default void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
		serializer.write(getValue());
	}
}
