package com.leemon.wushiwan.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @description: 客户端请求的结构体
 * @author: limeng
 * @create: 2019-03-26 23:08
 **/
@Data
@Accessors(chain = true)
public class RequestModal<T> {
	@NotNull
	private T data;
}
