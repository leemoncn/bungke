package com.leemon.wushiwan.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.leemon.wushiwan.config.fastjson.CustomFastJsonConfig;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: limeng
 * @create: 2019-03-25 22:40
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		FastJsonHttpMessageConverter fastHttpMessageConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig fastJsonConfig = CustomFastJsonConfig.getFastJsonConfig();
		fastHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		fastHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
		return new HttpMessageConverters((HttpMessageConverter<?>) fastHttpMessageConverter);
	}
}
