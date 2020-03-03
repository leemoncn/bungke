package com.leemon.wushiwan.dto;

import com.leemon.wushiwan.enums.PushPayloadType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 提示的payload
 * @author: limeng
 * @create: 2019-12-12 22:45
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class PushPayloadNotice extends PushModelPayload {
	//提示信息
	private String msg;

	public PushPayloadNotice() {
		setType(PushPayloadType.NOTICE);
	}
}
