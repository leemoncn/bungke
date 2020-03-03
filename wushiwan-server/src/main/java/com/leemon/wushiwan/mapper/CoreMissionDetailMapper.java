package com.leemon.wushiwan.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.dto.FinishMissionInfo;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.dto.MyPublishedMission;
import com.leemon.wushiwan.entity.CoreMissionDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Repository
public interface CoreMissionDetailMapper extends BaseMapper<CoreMissionDetail> {

	IPage<MissionDetail> selectCoreMissionDetailsByTypePropertyIdWithoutMyMissionAndAcceptedMission(
			Page page,
			@Param("typePropertyId") Integer typePropertyId,
			@Param("isOrderByYouxian") Boolean isOrderByYouxian,
			@Param("isOrderByRenqi") Boolean isOrderByRenqi,
			@Param("userId") Integer userId, @Param("deviceType") Integer deviceType, @Param("searchData") String searchData);

	MissionDetail selectMissionDetailByMissionId(Integer missionId);

	MissionDetail selectUsableRuleMissionDetailByMissionId(Integer missionId);

	/**
	 * 调整任务数量，changeCount为变动值，可以为正数或者负数
	 *
	 * @param changeCount
	 * @param missionDetailId
	 */
	void modifyMissionCount(@Param("changeCount") int changeCount, @Param("missionDetailId") Integer missionDetailId);

	/**
	 * 查询我发布的任务
	 *
	 * @param page
	 * @param userId
	 * @return
	 */
	IPage<MyPublishedMission> selectMyPublishedMissions(Page page, @Param("userId") Integer userId);

	FinishMissionInfo selectCoreMissionDetailByAcceptId(@Param("acceptId") Integer acceptId);

	List<MissionDetail> searchMissionDetail(Page page, @Param("statusPropertyId") Integer statusPropertyId, @Param("publishUserId") Integer publishUserId);

	List<MissionDetail> selectAllMissionOverDeadline();
}
