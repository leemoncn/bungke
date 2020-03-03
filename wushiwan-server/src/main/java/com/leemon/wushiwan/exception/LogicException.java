package com.leemon.wushiwan.exception;


import com.leemon.wushiwan.enums.base.ErrorCode;

/**
 * @description:
 * @author: limeng
 * @create: 2019-03-24 13:41
 **/
public class LogicException extends RuntimeException {

	private ErrorCode errorCode;

	public LogicException(ErrorCode errCode, String exceptionMessage) {
		super(exceptionMessage);
		this.errorCode = errCode;
	}

	public LogicException(ErrorCode errCode, String exceptionMessage, Object... args) {
		super(String.format(exceptionMessage, args));
		this.errorCode = errCode;
	}

	public LogicException(String exceptionMessage) {
		super(exceptionMessage);
	}

	public LogicException(String exceptionMessage, Object... args) {
		super(String.format(exceptionMessage, args));
	}

	public LogicException(ErrorCode errCode) {
		super(errCode.getErrorDescription());
		this.errorCode = errCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
