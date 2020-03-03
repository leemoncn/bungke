package com.leemon.wushiwan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * 由于spring security有一些路径是被ignore的，所以ignore的走MVC的cors，其他的走spring security的cors
 */
@Configuration
public class CorsConfig {
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = CorsConfig.createCorsConfig();
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	public static UrlBasedCorsConfigurationSource createCorsConfig() {
		final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.setMaxAge((long) 60 * 60 * 24 * 30);
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return urlBasedCorsConfigurationSource;
	}
}
