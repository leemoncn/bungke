package com.leemon.wushiwan.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.leemon.wushiwan.config.fastjson.CustomFastJsonConfig;
import com.leemon.wushiwan.entity.BaseModel;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.util.ExceptionResultUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 用于捕获过滤器直接抛出的异常，过滤器还没有进controller，所以不会触发ExceptionController
 * @author: leemon
 * @create: 2019-03-27 15:34
 **/
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			setErrorResponse(request, response, e);
		}
	}

	private void setErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e) {
		boolean isBusinessException = e instanceof LogicException;
		ExceptionResultUtil.logExceptionMsg(request, e, e instanceof LogicException);
		BaseModel resultModal;
		if (isBusinessException) {
			resultModal = ExceptionResultUtil.createFailedBaseModel((LogicException) e);
		} else {
			resultModal = ExceptionResultUtil.createFailedBaseModel(ErrorCode.SYS_ERROR, null);
		}
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
		FastJsonConfig config = CustomFastJsonConfig.getFastJsonConfig();
		String jsonString = JSONObject.toJSONString(resultModal, config.getSerializerFeatures());
		try {
			response.getWriter().write(jsonString);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
