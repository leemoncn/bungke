package com.leemon.wushiwan.config.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.baomidou.mybatisplus.core.enums.IEnum;

import java.lang.reflect.Type;

/**
 * @description: 枚举的反序列化自定义
 * @author: leemon
 * @create: 2019-03-26 15:36
 **/
public class PropertyEnumDeserializer implements ObjectDeserializer {

	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		Integer intValue = parser.parseObject(int.class);
		Class cls = (Class) type;
		Object[] enumConstants = cls.getEnumConstants();
//		if (EnumValue.class.isAssignableFrom(cls)) {
		for (Object enumConstant : enumConstants) {
			if (((IEnum) enumConstant).getValue() == intValue) {
				return (T) enumConstant;
			}
		}
//		}
		return null;
	}

	@Override
	public int getFastMatchToken() {
		return JSONToken.LITERAL_INT;
	}
}
