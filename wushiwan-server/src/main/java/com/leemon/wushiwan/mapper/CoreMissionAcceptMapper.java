package com.leemon.wushiwan.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.controller.CoreMissionController;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.dto.MyAcceptMission;
import com.leemon.wushiwan.entity.CoreMissionAccept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.vo.MissionAcceptVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Component
public interface CoreMissionAcceptMapper extends BaseMapper<CoreMissionAccept> {
	/**
	 * 查询自己的接单记录，并根据任务完成情况进行排序
	 *
	 * @param userId
	 * @param proceedPropertyId
	 * @return
	 */
	IPage<MyAcceptMission> selectMyAcceptMissionOrderByProceedPropertyId(Page page, @Param("userId") Integer userId, @Param("proceedPropertyId") Integer proceedPropertyId);

	/**
	 * 更新所有已经超过截止日期的任务，更新执行状态,missionId传null更新所有
	 */
	void updateAllMissionOverDeadline(@Param("missionId") Integer missionId);

	/**
	 * 更新所有做任务超时的任务，更新执行状态,userId传null更新所有
	 */
	void updateAllMissionTimeout(@Param("timeoutSecond") Integer timeoutSecond, @Param("userId") Integer userId);

	/**
	 * 更新所有不合格后没再重新提交的任务，更新finish_time,userId传null更新所有
	 */
	void updateAllMissionNotRecommit(@Param("timeoutSecond") Integer timeoutSecond, @Param("userId") Integer userId);

	/**
	 * 用户上传完成任务的凭证，并将任务置为审核中
	 *
	 * @param userId
	 * @param missionId
	 * @param textVerify
	 */
	void updateMissionTextVerify(@Param("userId") Integer userId, @Param("missionId") Integer missionId, @Param("textVerify") String textVerify);

	/**
	 * 更新已发布任务的审核结果,并更新剩余任务数量以及任务状态（如果剩余未0更新为任务全部完成）
	 *
	 * @param missionAcceptId
	 * @param result
	 */
	int updateMissionReviewResult(@Param("missionAcceptId") Integer missionAcceptId, @Param("result") Boolean result, @Param("updater") String updater, @Param("isFromComplaint") Boolean isFromComplaint);

	/**
	 * 发布任务者查询自己发布的某个任务的提交审核列表
	 *
	 * @param missionId         任务ID
	 * @param proceedPropertyId
	 * @param publishUserId
	 * @return
	 */
	IPage<CoreMissionController.ReviewMissionListResponse> selectReviewMissionList(Page page,
																				   @Param("missionId") Integer missionId,
																				   @Param("proceedPropertyId") Integer proceedPropertyId,
																				   @Param("publishUserId") Integer publishUserId);

	/**
	 * 获取正在进行中的已接取任务列表，状态是待提交和待审核的
	 *
	 * @param missionId
	 * @param publishUserId
	 * @return
	 */
	List<CoreMissionAccept> selectInProgressAcceptList(@Param("missionId") Integer missionId, @Param("publishUserId") Integer publishUserId);

	MissionAcceptVO selectMissionAcceptInfo(@Param("acceptId") Integer acceptId);

	List<CoreMissionAccept> selectAllReviewTimeoutMission(@Param("timeoutSecond") Integer timeoutSecond);

	/**
	 * 获取一个需要被审核的missionaccept id
	 *
	 * @param missionId
	 * @return
	 */
	Integer getOneNeedReviewAccept(@Param("missionId") Integer missionId);
}
