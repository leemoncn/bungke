package com.leemon.wushiwan.dto;

import com.leemon.wushiwan.enums.PushPayloadType;
import lombok.Data;

/**
 * @description: 只能用这个类的子类初始化
 * @author: limeng
 * @create: 2019-12-12 22:42
 **/
@Data
public class PushModelPayload {
	private PushPayloadType type;

	protected PushModelPayload() {
	}
}
