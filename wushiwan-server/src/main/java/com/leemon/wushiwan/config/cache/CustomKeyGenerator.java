package com.leemon.wushiwan.config.cache;

import com.leemon.wushiwan.system.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @description: 自定义springcache的key的生成策略
 * @author: leemon
 * @create: 2019-04-25 09:16
 **/
@Slf4j
public class CustomKeyGenerator implements KeyGenerator {

	@SuppressWarnings("NullableProblems")
	@Override
	public Object generate(Object target, Method method, Object... params) {
		return target.getClass().getSimpleName() + "_"
				+ method.getName() + "_"
				+ StringUtils.arrayToDelimitedString(params, "_");
	}
}
