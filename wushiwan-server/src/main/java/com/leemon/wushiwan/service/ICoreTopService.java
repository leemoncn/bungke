package com.leemon.wushiwan.service;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreTop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-07-25
 */
public interface ICoreTopService extends IService<CoreTop> {

	/**
	 * 添加任务置顶记录
	 *
	 * @param missionId
	 */
	void addTopMission(Integer missionId, Integer hour, MissionDetail missionDetail);

	int getFreeHourToday();
}
