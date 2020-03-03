package com.leemon.wushiwan.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @description:
 * @author: limeng
 * @create: 2019-08-15 21:33
 **/
@Data
public class ExplainData implements Serializable {
	private String text;
	private String img;
}
