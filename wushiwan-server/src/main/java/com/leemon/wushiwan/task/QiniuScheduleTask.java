package com.leemon.wushiwan.task;

import com.leemon.wushiwan.service.QiniuKodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: 每分钟检查用户接取的任务是否超过了截止日期，以及做任务时间是否超时
 * @author: leemon
 * @create: 2019-05-28 16:20
 **/
@Component
@Slf4j
public class QiniuScheduleTask {
	private final QiniuKodoService kodoService;

	public QiniuScheduleTask(QiniuKodoService kodoService) {
		this.kodoService = kodoService;
	}

	@Scheduled(cron = "0 30 3 * * ?")//每天凌晨3点半执行
	private void configureTasks() {
		kodoService.deleteTimeoutUpload();
	}
}
