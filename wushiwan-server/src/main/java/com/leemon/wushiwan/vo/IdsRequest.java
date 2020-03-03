package com.leemon.wushiwan.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @description:
 * @author: leemon
 * @create: 2019-04-24 11:32
 **/
@Data
public class IdsRequest {
	@NotBlank
	private String ids;
}
