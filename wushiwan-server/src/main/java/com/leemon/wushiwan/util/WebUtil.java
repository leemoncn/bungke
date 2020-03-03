package com.leemon.wushiwan.util;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-27 13:34
 **/
@Component
public class WebUtil {

	private HttpServletRequest request;

	@Autowired
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getClientIp() {

		String remoteAddr = "";

		if (request != null) {
			remoteAddr = request.getHeader("X-Real-IP");
			if (Strings.isNullOrEmpty(remoteAddr)) {
				remoteAddr = request.getHeader("X-FORWARDED-FOR");
				if (Strings.isNullOrEmpty(remoteAddr)) {
					remoteAddr = request.getRemoteAddr();
				}
			}
		}
		return remoteAddr;
	}
}
