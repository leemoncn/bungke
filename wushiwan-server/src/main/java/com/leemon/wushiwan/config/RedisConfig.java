package com.leemon.wushiwan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-20 13:42
 **/
@Configuration
public class RedisConfig {

	/**
	 * retemplate相关配置
	 * @param factory
	 * @return
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		// 配置连接工厂
		redisTemplate.setConnectionFactory(factory);

		//使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
//		FastJsonRedisSerializer fastjsonSerializer = new FastJsonRedisSerializer<>(Object.class);

//		ObjectMapper om = new ObjectMapper();
//		// 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
//		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//		// 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
//		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//		fastjsonSeial.setObjectMapper(om);
//		fastjsonSerializer.setFastJsonConfig(CustomFastJsonConfig.getFastJsonConfig());

		// 值采用json序列化
//		template.setValueSerializer(fastjsonSerializer);
		//使用StringRedisSerializer来序列化和反序列化redis的key值
		redisTemplate.setKeySerializer(new StringRedisSerializer());

		// 设置hash key 和value序列化模式
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//		template.setHashValueSerializer(fastjsonSerializer);
//		template.afterPropertiesSet();

		return redisTemplate;
	}

	/**
	 * 对hash类型的数据操作
	 *
	 * @param redisTemplate
	 * @return
	 */
	@Bean
	public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForHash();
	}

	/**
	 * 对redis字符串类型数据操作
	 *
	 * @param redisTemplate
	 * @return
	 */
	@Bean
	public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForValue();
	}

	/**
	 * 对链表类型的数据操作
	 *
	 * @param redisTemplate
	 * @return
	 */
	@Bean
	public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForList();
	}

	/**
	 * 对无序集合类型的数据操作
	 *
	 * @param redisTemplate
	 * @return
	 */
	@Bean
	public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForSet();
	}

	/**
	 * 对有序集合类型的数据操作
	 *
	 * @param redisTemplate
	 * @return
	 */
	@Bean
	public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForZSet();
	}
}
