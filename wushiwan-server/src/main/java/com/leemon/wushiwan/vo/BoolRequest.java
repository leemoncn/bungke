package com.leemon.wushiwan.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @description: 只有一个布尔值的请求
 * @author: limeng
 * @create: 2019-05-29 22:32
 **/
public class BoolRequest {
	@NotNull
	private Boolean result;
}
