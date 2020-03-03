package com.leemon.wushiwan.service;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.system.entity.SysUser;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ICoreMissionService extends IService<CoreMission> {

	/**
	 * 任务下架
	 *
	 * @param missionDetail
	 * @param user
	 * @return 退回的任务币数量
	 */
	int offMission(MissionDetail missionDetail, SysUser user);
}
