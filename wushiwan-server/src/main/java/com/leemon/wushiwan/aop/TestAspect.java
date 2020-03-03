package com.leemon.wushiwan.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-20 15:12
 **/
@Slf4j
@Aspect
@Component
public class TestAspect {

	@Before("execution(* com.leemon.wushiwan.system.controller.SysUserController.aaa(..))")
	public void before() {
		log.info("aaaaaaa999");
	}
}
