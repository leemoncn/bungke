package com.leemon.wushiwan.entity;

import com.leemon.wushiwan.enums.base.ErrorCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: 向前端返回结果时的基础类
 * @author: leemon
 * @create: 2019-03-25 10:28
 **/
@Data
@Accessors(chain = true)
public class BaseModel implements Serializable {
	private String code;
	private String msg;
	private Object data;

	public void initWithSuccessData(Object data){
		this.setCode(ErrorCode.SUCCESS.name());
		this.setMsg("");
		this.setData(data);
	}
}
