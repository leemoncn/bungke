package com.leemon.wushiwan.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @description:
 * @author: limeng
 * @create: 2019-03-25 07:08
 **/
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor().setOverflow(true);
	}

	@Bean
	public ISqlInjector sqlInjector() {
		return new LogicSqlInjector();
	}
}
