package com.leemon.wushiwan.util;

import com.leemon.wushiwan.entity.BaseModel;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: 根据异常生成返回信息
 * @author: leemon
 * @create: 2019-03-27 15:46
 **/
@Slf4j
public class ExceptionResultUtil {
	public static BaseModel createSuccessBaseModel(Object data) {
		BaseModel model = new BaseModel();
		model.initWithSuccessData(data);
		return model;
	}

	public static BaseModel createFailedBaseModel(LogicException e) {
		ErrorCode expCode = e.getErrorCode();
		if (expCode == null) {
			return createFailedBaseModel(ErrorCode.SYS_ERROR, null, e.getMessage());
		}
		return createFailedBaseModel(expCode, null);
	}

	public static BaseModel createFailedBaseModel(ErrorCode code, Object data) {
		return createFailedBaseModel(code, data, code.getErrorDescription());
	}

	public static BaseModel createFailedBaseModel(ErrorCode code, Object data, String msg) {
		BaseModel model = new BaseModel();
		model.setCode(code.name());
		model.setMsg(msg);
		model.setData(data);
		return model;
	}

	public static void logExceptionMsg(HttpServletRequest req, Exception e, boolean businessException) {
		if (businessException) {
			log.info("出现业务异常,请求路径:{},异常信息:{},堆栈：", req.getRequestURI(), e.getMessage(), e);
		} else {
			log.error("出现系统异常,请求路径:{},异常信息:{},堆栈：", req.getRequestURI(), e.getMessage(), e);
		}
	}
}
