package com.leemon.wushiwan.task;

import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.mapper.CoreMissionAcceptMapper;
import com.leemon.wushiwan.service.ICoreMissionAcceptService;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: 每分钟检查用户接取的任务是否超过了截止日期，以及做任务时间是否超时
 * @author: leemon
 * @create: 2019-05-28 16:20
 **/
@Component
@Slf4j
public class DeadlineTimeoutScheduleTask {

	private final CoreMissionAcceptMapper coreMissionAcceptMapper;
	private final ICoreMissionDetailService coreMissionDetailService;
	private final ICoreMissionAcceptService acceptService;
	private final Config config;

	@Autowired
	public DeadlineTimeoutScheduleTask(CoreMissionAcceptMapper coreMissionAcceptMapper, ICoreMissionDetailService coreMissionDetailService, ICoreMissionAcceptService acceptService, Config config) {
		this.coreMissionAcceptMapper = coreMissionAcceptMapper;
		this.coreMissionDetailService = coreMissionDetailService;
		this.acceptService = acceptService;
		this.config = config;
	}


	@Scheduled(cron = "0 0/1 * * * ?")//每分钟执行一次
	private void configureTasks() {
		//更新所有超过截止日期的任务
		coreMissionDetailService.updateAllMissionOverDeadline();
		//更新所有做任务超时的任务(20分钟)
		coreMissionAcceptMapper.updateAllMissionTimeout(config.getMissionTimeOutSecond(), null);
		//更新所有任务不合格，但是12小时内没有重新提交的任务
		coreMissionAcceptMapper.updateAllMissionNotRecommit(config.getMissionRejectRecommitSecond(), null);
		//更新24小时还未审核的任务，将任务更改为已完成,并给用户发钱
		acceptService.updateAllReviewMissionTimeout();
	}
}
