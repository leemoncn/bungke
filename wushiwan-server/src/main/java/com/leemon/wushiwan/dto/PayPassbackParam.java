package com.leemon.wushiwan.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.config.fastjson.PropertyEnumDeserializer;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.PayType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @description: 支付时的透传字段
 * @author: limeng
 * @create: 2019-07-14 14:15
 **/
@Data
@Slf4j
public class PayPassbackParam implements Serializable {
	private Integer userId;
	private PayType payType;
	private String data;
}
