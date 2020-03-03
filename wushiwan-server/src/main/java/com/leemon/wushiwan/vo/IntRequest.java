package com.leemon.wushiwan.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 只有一个int的请求
 * @author: limeng
 * @create: 2019-05-22 20:45
 **/
@Data
public class IntRequest {
	@NotNull
	private Integer value;
}
