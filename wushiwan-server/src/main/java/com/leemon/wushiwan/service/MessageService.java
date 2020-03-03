package com.leemon.wushiwan.service;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.google.common.base.Strings;
import com.leemon.wushiwan.config.MessageConfig;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @description: 发送短信服务
 * @author: limeng
 * @create: 2019-06-20 20:29
 **/
@Slf4j
@Component
public class MessageService {

	private final MessageConfig messageConfig;
	private final RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public MessageService(MessageConfig messageConfig, RedisTemplate<String, Object> redisTemplate) {
		this.messageConfig = messageConfig;
		this.redisTemplate = redisTemplate;
	}

	public void sendMessage(String phone, String code) {
		if (Strings.isNullOrEmpty(phone)) {
			throw new LogicException("需要手机号");
		}
		if (phone.length() != 11) {
			throw new LogicException("手机号不合法");
		}
		if (Strings.isNullOrEmpty(code) || code.length() != 6) {
			throw new LogicException(ErrorCode.SYS_ERROR, "需要6位随机数");
		}
		SmsSingleSenderResult result;
		try {
			String[] params = {code, messageConfig.getValidMinute() + ""};
			SmsSingleSender sender = new SmsSingleSender(messageConfig.getAppid(), messageConfig.getAppkey());
			result = sender.sendWithParam("86", phone,
					messageConfig.getTemplateId(), params, messageConfig.getSmsSign(), "", "");
		} catch (Exception e) {
			throw new LogicException(ErrorCode.SYS_ERROR, "调用发送短信API失败,err = " + e);
		}
		if (result.result != 0) {
			if (result.result == 1016 || result.result == 1024 || result.result == 1023) {
				throw new LogicException(result.errMsg);
			}
			throw new LogicException(ErrorCode.SYS_ERROR, "发送短信失败,result = " + result + ",errmsg = " + result.errMsg);
		}
		log.info("{}调用短信发送服务,code = {}", phone, code);
		saveToRedis(phone, code);
	}

	public String sendMessage(String phone) {
		String code = RandomStringUtils.randomNumeric(6);
		this.sendMessage(phone, code);
		return code;
	}

	public String getCode(String phone) {
		if (Strings.isNullOrEmpty(phone)) {
			return null;
		}
		return Optional.ofNullable(redisTemplate.opsForValue().get(getRedisKey(phone)))
				.map(Object::toString).orElse(null);
	}

	public void destroyCacheCode(String phone) {
		if (Strings.isNullOrEmpty(phone)) {
			return;
		}
		redisTemplate.delete(getRedisKey(phone));
	}

	private void saveToRedis(String phone, String code) {
		String key = getRedisKey(phone);
		redisTemplate.opsForValue().set(key, code, messageConfig.getValidMinute(), TimeUnit.MINUTES);
	}

	private String getRedisKey(String phone) {
		return "sms_" + phone;
	}
}
