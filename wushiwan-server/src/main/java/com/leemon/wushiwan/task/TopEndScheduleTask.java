package com.leemon.wushiwan.task;

import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.mapper.CoreMissionAcceptMapper;
import com.leemon.wushiwan.mapper.CoreMissionMapper;
import com.leemon.wushiwan.service.ICoreMissionAcceptService;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: 每分钟检查任务是否已经过了置顶时间
 * @author: leemon
 * @create: 2019-05-28 16:20
 **/
@Component
@Slf4j
public class TopEndScheduleTask {

	private final CoreMissionMapper missionMapper;
	private final Config config;

	@Autowired
	public TopEndScheduleTask(CoreMissionMapper missionMapper, Config config) {
		this.missionMapper = missionMapper;
		this.config = config;
	}


	@Scheduled(cron = "0 0/1 * * * ?")//每分钟执行一次
	private void configureTasks() {
		missionMapper.updateAllMissionOverTopEnd();
	}
}
