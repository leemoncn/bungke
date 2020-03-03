package com.leemon.wushiwan.config.fastjson;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-26 16:32
 **/
public class CustomFastJsonConfig {
	private static final FastJsonConfig fastJsonConfig;
	static {
		fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteEnumUsingToString);
		fastJsonConfig.setParserConfig(new CustomParserConfig());
	}

	public static FastJsonConfig getFastJsonConfig(){
		return fastJsonConfig;
	}
}
