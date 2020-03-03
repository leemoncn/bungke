package com.leemon.wushiwan.controller;

import com.leemon.wushiwan.entity.BaseModel;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.util.ExceptionResultUtil;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 统一处理controller中的异常以及返回值的封装.
 * @author: limeng
 * @create: 2018-08-31 14:07
 **/
@Slf4j
@ControllerAdvice
public class ExceptionController implements ResponseBodyAdvice {

	@Override
	public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class aClass) {
		//拦截所有请求
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object o, @NonNull MethodParameter methodParameter, @NonNull MediaType mediaType, @NonNull Class aClass, @NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse) {
		HttpServletResponse servletResponse = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
		if (servletResponse.getStatus() == HttpStatus.FORBIDDEN.value()) {//403
			return ExceptionResultUtil.createFailedBaseModel(ErrorCode.LOGIN_FAILED, null);
		}
		if (servletResponse.getStatus() != HttpStatus.OK.value()) {
			return ExceptionResultUtil.createFailedBaseModel(ErrorCode.STATUS_ERROR, o);
		}
		if (o == null) {
			return ExceptionResultUtil.createSuccessBaseModel(o);
		}
		if (o.getClass() == BaseModel.class) {
			return o;
		}
		if (mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
			return ExceptionResultUtil.createSuccessBaseModel(o);
		}
		return o;
	}


	/**
	 * 如果只传了ErrorCode，则给前端返回ErrorCode的错误描述
	 * 如果只传了msg，则给前端返回SYS_ERROR和msg
	 * 如果全都传了，则给前端返回ErrorCode，msg只打印
	 *
	 * @param e
	 * @param req
	 * @return
	 */
	@ExceptionHandler
	@ResponseBody
	public Object handleException(LogicException e, HttpServletRequest req) {
		ExceptionResultUtil.logExceptionMsg(req, e, true);
		return ExceptionResultUtil.createFailedBaseModel(e);
	}

	@ExceptionHandler
	@ResponseBody
	public Object handleException(Exception e, HttpServletRequest req) {
		ExceptionResultUtil.logExceptionMsg(req, e, false);
		return ExceptionResultUtil.createFailedBaseModel(ErrorCode.SYS_ERROR, null);
	}

	@ExceptionHandler
	@ResponseBody
	public Object handleException(BindException e, HttpServletRequest req) {
		ExceptionResultUtil.logExceptionMsg(req, e, false);
		return ExceptionResultUtil.createFailedBaseModel(ErrorCode.PARAMS_INVALID, null);
	}

	@ExceptionHandler
	@ResponseBody
	public Object handleException(HttpMessageNotReadableException e, HttpServletRequest req) {
		ExceptionResultUtil.logExceptionMsg(req, e, false);
		return ExceptionResultUtil.createFailedBaseModel(ErrorCode.PARAMS_INVALID, null);
	}

	@ExceptionHandler
	@ResponseBody
	public Object handleException(MethodArgumentNotValidException e, HttpServletRequest req) {
		ExceptionResultUtil.logExceptionMsg(req, e, false);
		return ExceptionResultUtil.createFailedBaseModel(ErrorCode.PARAMS_INVALID, null);
	}

	@ExceptionHandler
	@ResponseBody
	public Object handleException(SignatureException e, HttpServletRequest req) {//jwt验证签名错误
		ExceptionResultUtil.logExceptionMsg(req, e, false);
		return ExceptionResultUtil.createFailedBaseModel(ErrorCode.LOGIN_FAILED, null);
	}

	@ExceptionHandler
	@ResponseBody
	public Object handleException(HttpMediaTypeNotSupportedException e, HttpServletRequest req) {
		ExceptionResultUtil.logExceptionMsg(req, e, false);
		return ExceptionResultUtil.createFailedBaseModel(ErrorCode.PARAMS_INVALID, null);
	}

	@ExceptionHandler
	@ResponseBody
	public Object handleException(AccessDeniedException e, HttpServletRequest req) {
		ExceptionResultUtil.logExceptionMsg(req, e, false);
		return ExceptionResultUtil.createFailedBaseModel(ErrorCode.NO_AUTH, null);
	}

}
