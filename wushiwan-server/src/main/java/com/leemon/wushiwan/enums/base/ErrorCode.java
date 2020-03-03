package com.leemon.wushiwan.enums.base;

/**
 * @description:
 * @author: limeng
 * @create: 2019-03-24 13:54
 **/
public enum ErrorCode {
	SUCCESS(""),//成功
	SYS_ERROR("系统错误"),//系统内部逻辑错误
	LOGIN_FAILED("请重新登录"),
	STATUS_ERROR("状态码错误"),//状态码不是200，需要检查具体状态码，在data字段里面
	PARAMS_INVALID("参数错误"),
	ROLE_DISABLED("当前角色已被禁用"),
	NO_AUTH("权限不足，禁止访问"),//调用我们的API接口出现参数错误
	REGIST_ACCOUNT_EXIST("账号已存在"),//注册时账号已存在
	PAY_ERROR("支付遇到问题，请稍后再试或联系客服");//注册时账号已存在

	private String errorDescription;

	ErrorCode(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}
