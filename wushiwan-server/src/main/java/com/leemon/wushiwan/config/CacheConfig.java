package com.leemon.wushiwan.config;

import com.leemon.wushiwan.config.cache.CustomKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;


/**
 * @description: 缓存配置
 * @author: limeng
 * @create: 2019-03-24 21:19
 **/
@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
										  ResourceLoader resourceLoader) {
		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
				.builder(redisConnectionFactory)
				.cacheDefaults(determineConfiguration(resourceLoader.getClassLoader()));
		return builder.build();
	}


	private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
			ClassLoader classLoader) {
		org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
				.defaultCacheConfig();
		config = config.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new JdkSerializationRedisSerializer(classLoader)))
				//redis缓存2小时过期
				.entryTtl(Duration.ofHours(2));
		return config;
	}

	@Override
	public KeyGenerator keyGenerator() {
		return new CustomKeyGenerator();
	}
}
