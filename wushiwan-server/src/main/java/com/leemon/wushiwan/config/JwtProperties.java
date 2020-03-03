package com.leemon.wushiwan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: Jwt配置文件
 * @author: limeng
 * @create: 2019-03-24 09:25
 **/
//@Data
//@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private String header = "Authorization";
	private String secret = "fuck.secret";
	private int expiration = 604800;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}
}
