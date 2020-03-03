package com.leemon.wushiwan.config;

import com.github.qcloudsms.SmsSingleSenderResult;
import com.leemon.wushiwan.mapper.CoreMissionAcceptMapper;
import com.leemon.wushiwan.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.util.ResourceBundle;

/**
 * @description: 定义一些在boot启动完成后执行的操作
 * @author: limeng
 * @create: 2019-03-24 21:14
 **/
@Slf4j
@Component
public class AfterBootStarted implements ApplicationRunner {

	private final Config config;
	private final CoreMissionAcceptMapper coreMissionAcceptMapper;
	private final MessageService messageService;

	@Autowired
	public AfterBootStarted(Config config, CoreMissionAcceptMapper coreMissionAcceptMapper, MessageService messageService) {
		this.config = config;
		this.coreMissionAcceptMapper = coreMissionAcceptMapper;
		this.messageService = messageService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//下面日志不能去掉，jenkins会检测这句话
		log.info("wushiwan启动完成");
	}
}
