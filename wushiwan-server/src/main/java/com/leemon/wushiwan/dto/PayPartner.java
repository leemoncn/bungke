package com.leemon.wushiwan.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @description: 充值合作商时的相关信息
 * @author: limeng
 * @create: 2019-10-22 20:44
 **/
@Data
@Accessors(chain = true)
public class PayPartner {
	private Boolean isYear;
	private Integer partnerId;
}
