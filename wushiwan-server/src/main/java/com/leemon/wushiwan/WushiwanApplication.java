package com.leemon.wushiwan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties
@MapperScan("com.leemon.wushiwan.**.mapper")
@ServletComponentScan
@EnableAspectJAutoProxy
@SpringBootApplication
public class WushiwanApplication {
	public static void main(String[] args) {
		SpringApplication.run(WushiwanApplication.class, args);
	}
}
