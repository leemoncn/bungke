package com.leemon.wushiwan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMissionDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.enums.DeviceType;
import com.leemon.wushiwan.system.entity.SysUser;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ICoreMissionDetailService extends IService<CoreMissionDetail> {

	/**
	 * 查询某一种类型的，当前可接取的所有任务,不包括我发布的任务，以及我已经截取了但还没做完的任务,以及审核驳回了但还没超过12小时的
	 *
	 * @param page
	 * @param typePropertyId   类型ID
	 * @param isOrderByYouxian
	 * @param isOrderByRenqi
	 * @param userId
	 * @param deviceType
	 * @param searchData
	 * @return
	 */
	IPage<MissionDetail> getCoreMissionDetailsByTypePropertyIdWithoutMyMissionAndAcceptedMission(Page<MissionDetail> page, Integer typePropertyId, Boolean isOrderByYouxian, Boolean isOrderByRenqi, Integer userId, DeviceType deviceType, String searchData);

	/**
	 * 根据id查找任务详情，且保证这个任务一定是存在且任务状态是已发布状态 MissionPublishType.PUBLISHED，如果不是这种状态，就抛异常
	 *
	 * @param missionId
	 * @return
	 */
	MissionDetail getExistAndPublishedMissionDetailByMissionId(int missionId);

	/**
	 * 根据id查找任务详情，且保证这个任务一定是存在且任务状态是已发布状态 MissionPublishType.PUBLISHED，并且在core_mission_rule中这类型任务时可用状态，如果不是这种状态，就抛异常
	 *
	 * @param missionId
	 * @return
	 */
	MissionDetail getExistAndPublishedAndUsableRuleMissionDetailByMissionId(int missionId);

	/**
	 * 根据id查找任务详情
	 *
	 * @param missionId
	 * @return
	 */
	MissionDetail getMissionDetailByMissionId(int missionId);

	/**
	 * 追加任务数量
	 *
	 * @param sysUser
	 * @param missionDetail
	 * @param count         变化的数量
	 */
	void addMissionCount(SysUser sysUser, MissionDetail missionDetail, int count);

	/**
	 * 上调任务出价
	 *
	 * @param sysUser
	 * @param missionDetail
	 * @param price         调整后的价格
	 */
	void addMissionPrice(SysUser sysUser, MissionDetail missionDetail, int price);

	/**
	 * 更新所有超过截止时间的任务，将任务更改为截止，并把剩余没做完的任务币退还
	 */
	void updateAllMissionOverDeadline();
}
