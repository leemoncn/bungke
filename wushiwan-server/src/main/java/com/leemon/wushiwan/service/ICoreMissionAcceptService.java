package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreMissionAccept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.vo.MissionAcceptVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ICoreMissionAcceptService extends IService<CoreMissionAccept> {

	/**
	 * 用户接取了某一个任务
	 *
	 * @param missionId     任务ID
	 * @param acceptUserId  接取者ID
	 * @param publishUserId
	 */
	void acceptMission(Integer missionId, Integer acceptUserId, Integer publishUserId);

	/**
	 * 用户是否已经接取过这个任务，且此任务还在进行中
	 *
	 * @param missionId
	 * @param userId
	 * @return
	 */
	boolean isAcceptMission(Integer missionId, Integer userId);

	/**
	 * 查询可以提交的任务，状态是待提交，或者状态是不合格但是finish_time还是null
	 *
	 * @param userId
	 * @param missionId
	 * @return
	 */
	CoreMissionAccept getCanCommitMission(Integer userId, Integer missionId);

	/**
	 * 查询一个已接取的任务，包括他的申诉状态
	 *
	 * @param acceptId
	 * @return
	 */
	MissionAcceptVO getCoreMissionAcceptById(Integer acceptId);

	/**
	 * 任务发布者审核任务
	 *  @param missionAcceptId
	 * @param result
	 * @param reason          用户自己审核不合格时，可以填写原因，会自动合并到聊天记录里面
	 * @param redisKey         用户自己审核不合格时，传的图片
	 * @param isFromComplaint 通过申诉，官方处理的
	 * @param updater         如果是用户自己审核的，或者用户自己点了任务下架，那么这个值是null，如果是用户申诉，填入处理人（系统管理员）的昵称，如果是审核超时系统自动审核成功的，填入system
	 */
	void reviewMission(Integer missionAcceptId, Boolean result, String reason, String redisKey, Boolean isFromComplaint, String updater);

	/**
	 * 更新24小时还未审核的任务，将任务更改为已完成,并把钱发给用户
	 */
	void updateAllReviewMissionTimeout();

	/**
	 * 一个任务被封，会导致所有接取了任务的也变成被封状态
	 */
	void banMissionAccept(Integer missionId);
}
