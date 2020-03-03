package com.leemon.wushiwan.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @description: 前端像后端只传一个id的请求
 * @author: leemon
 * @create: 2019-04-24 11:03
 **/
@Data
public class IdRequest {
	@NotNull
	@Min(1)
	private Integer id;
}
