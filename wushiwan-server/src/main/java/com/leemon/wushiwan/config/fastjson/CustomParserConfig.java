package com.leemon.wushiwan.config.fastjson;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.baomidou.mybatisplus.core.enums.IEnum;

import java.lang.reflect.Type;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-26 15:54
 **/
public class CustomParserConfig extends ParserConfig {

	private static final PropertyEnumDeserializer propertyEnumDeserializer = new PropertyEnumDeserializer();

	@Override
	public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
		if (clazz.isEnum() && IEnum.class.isAssignableFrom(clazz)) {
			return propertyEnumDeserializer;
		}
		return super.getDeserializer(clazz, type);
	}
}
