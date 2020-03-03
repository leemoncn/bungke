package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.dto.FinishMissionInfo;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMissionAccept;
import com.leemon.wushiwan.entity.CoreMissionComplaint;
import com.leemon.wushiwan.enums.ChatStatusType;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.MissionProceedType;
import com.leemon.wushiwan.enums.NoticeType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreMissionAcceptMapper;
import com.leemon.wushiwan.mapper.CoreMissionDetailMapper;
import com.leemon.wushiwan.service.ICoreMissionAcceptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.service.ICoreMissionComplaintService;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.service.ISocialReviewChatService;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.RMBUtil;
import com.leemon.wushiwan.vo.MissionAcceptVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
public class CoreMissionAcceptServiceImpl extends ServiceImpl<CoreMissionAcceptMapper, CoreMissionAccept> implements ICoreMissionAcceptService {

	private final CoreMissionAcceptMapper coreMissionAcceptMapper;
	private final CoreMissionDetailMapper coreMissionDetailMapper;
	private final ISysUserService sysUserService;
	private final ISocialNoticeService socialNoticeService;
	private final ISocialReviewChatService chatService;
	private final ICoreMissionComplaintService complaintService;
	private final Config config;

	@Autowired
	public CoreMissionAcceptServiceImpl(CoreMissionAcceptMapper coreMissionAcceptMapper, CoreMissionDetailMapper coreMissionDetailMapper, ISysUserService sysUserService, ISocialNoticeService socialNoticeService, ISocialReviewChatService chatService, ICoreMissionComplaintService complaintService, Config config) {
		this.coreMissionAcceptMapper = coreMissionAcceptMapper;
		this.coreMissionDetailMapper = coreMissionDetailMapper;
		this.sysUserService = sysUserService;
		this.socialNoticeService = socialNoticeService;
		this.chatService = chatService;
		this.complaintService = complaintService;
		this.config = config;
	}

	@Override
	public void acceptMission(Integer missionId, Integer acceptUserId, Integer publishUserId) {
		CoreMissionAccept cma = new CoreMissionAccept();
		cma.setAcceptTime(LocalDateTime.now());
		cma.setMissionId(missionId);
		cma.setAcceptUserId(acceptUserId);
		cma.setPublishUserId(publishUserId);
		cma.setProceedPropertyId(MissionProceedType.WAIT_COMMIT);
		save(cma);
	}

	@Override
	public boolean isAcceptMission(Integer missionId, Integer userId) {
		QueryWrapper<CoreMissionAccept> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreMissionAccept::getMissionId, missionId)
				.eq(CoreMissionAccept::getAcceptUserId, userId)
				.and(i -> i.eq(CoreMissionAccept::getProceedPropertyId, MissionProceedType.WAIT_COMMIT).or().eq(CoreMissionAccept::getProceedPropertyId, MissionProceedType.IN_REVIEW));
		CoreMissionAccept cma = getOne(qw);
		return cma != null;
	}

	/**
	 * 查询可以提交的任务，状态是待提交，或者状态是不合格但是finish_time还是null
	 *
	 * @param userId
	 * @param missionId
	 * @return
	 */
	@Override
	public CoreMissionAccept getCanCommitMission(Integer userId, Integer missionId) {
		QueryWrapper<CoreMissionAccept> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreMissionAccept::getMissionId, missionId)
				.eq(CoreMissionAccept::getAcceptUserId, userId)
				.and(w -> w.eq(CoreMissionAccept::getProceedPropertyId, MissionProceedType.WAIT_COMMIT)
						.or(ww -> ww.eq(CoreMissionAccept::getProceedPropertyId, MissionProceedType.REJECTED)
								.isNull(CoreMissionAccept::getFinishTime))
				);
		return getOne(qw);
	}

	/**
	 * 查询一个已接取的任务，包括他的申诉状态
	 *
	 * @param acceptId
	 * @return
	 */
	@Override
	public MissionAcceptVO getCoreMissionAcceptById(Integer acceptId) {
		return coreMissionAcceptMapper.selectMissionAcceptInfo(acceptId);
	}

	/**
	 * 任务发布者审核任务,这里只保留用户来审核任务的结果，如果是用户申诉过，那么最终任务的结果在申诉表中
	 *
	 * @param missionAcceptId
	 * @param result
	 * @param reason          用户自己审核不合格时，可以填写原因，会自动合并到聊天记录里面
	 * @param redisKey        用户自己审核不合格时，传的图片
	 * @param isFromComplaint 通过申诉，官方处理的
	 * @param updater         如果是用户自己审核的，或者用户自己点了任务下架，那么这个值是null，如果是用户申诉，填入处理人（系统管理员）的昵称，如果是审核超时系统自动审核成功的，填入system
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void reviewMission(Integer missionAcceptId, Boolean result, String reason, String redisKey, Boolean isFromComplaint, String updater) {
		if (!isFromComplaint) {//不是通过申诉来的，需要先检查是否已经申诉过
			CoreMissionComplaint cmc = complaintService.getCoreMissionComplaintByMissionAcceptId(missionAcceptId);
			if (cmc != null) {
				if (cmc.getResult() == null) {
					throw new LogicException("该任务已申诉，请等待官方审核结果");
				}
				if (cmc.getResult()) {
					throw new LogicException("该任务官方终审合格");
				} else {
					throw new LogicException("该任务官方终审不合格");
				}
			}
		}
		FinishMissionInfo fmi = coreMissionDetailMapper.selectCoreMissionDetailByAcceptId(missionAcceptId);
		if (fmi == null) {
			throw new LogicException(ErrorCode.SYS_ERROR, "审核任务不存在");
		}
		//更新已发布任务的审核结果,并更新剩余任务数量以及任务状态（如果剩余为0更新为任务全部完成）
		coreMissionAcceptMapper.updateMissionReviewResult(missionAcceptId, result, updater, isFromComplaint);
		MissionDetail missionDetail = coreMissionDetailMapper.selectMissionDetailByMissionId(fmi.getMissionId());
		String msg;
		if (result) {//审核通过，给用户加钱
			sysUserService.updateUserEarning(sysUserService.getById(fmi.getAcceptMissionUserId()), CurrencyChangeReasonType.FINISH_MISSION, fmi.getPrice());
			msg = String.format("任务<%s>审核通过，获得收入%.02f元", missionDetail.getTitle(), RMBUtil.fenToYuan(fmi.getPrice()));
		} else {//不通过
			if (reason == null) {
				msg = String.format("任务<%s>审核不通过，原因：平台终审不合格", missionDetail.getTitle());
			} else {
				msg = String.format("任务<%s>审核不通过，原因：%s", missionDetail.getTitle(), reason);
			}

		}
		//发送通知
		socialNoticeService.sendNotice(NoticeType.RESULT_OF_FINISH_MISSION, msg, fmi.getAcceptMissionUserId());
		if (isFromComplaint) {//通过投诉处理的
			if (result) {
				chatService.addNewChat(reason, missionDetail, redisKey, null, fmi.getAcceptMissionUserId(), missionAcceptId, ChatStatusType.ComplainSuccess.getTitle());
			} else {
				chatService.addNewChat(reason, missionDetail, redisKey, null, fmi.getAcceptMissionUserId(), missionAcceptId, ChatStatusType.ComplainFailed.getTitle());
			}
		} else {//商家处理的
			if (result) {
				chatService.addNewChat(reason, missionDetail, redisKey, missionDetail.getUserId(), fmi.getAcceptMissionUserId(), missionAcceptId, ChatStatusType.MissionReviewSuccess.getTitle());
			} else {
				chatService.addNewChat(reason, missionDetail, redisKey, missionDetail.getUserId(), fmi.getAcceptMissionUserId(), missionAcceptId, ChatStatusType.MissionReviewFailed.getTitle());
			}
		}
	}

	/**
	 * 更新24小时还未审核的任务，将任务更改为已完成,并把钱发给用户
	 */
	@Override
	public void updateAllReviewMissionTimeout() {
		List<CoreMissionAccept> list = coreMissionAcceptMapper.selectAllReviewTimeoutMission(config.getMissionReviewHour() * 60 * 60);
		list.forEach(cma -> reviewMission(cma.getId(), true, null, null, false, "system"));
	}

	/**
	 * 一个任务被封，会导致所有接取了任务的也变成被封状态
	 */
	@Override
	public void banMissionAccept(Integer missionId) {
		UpdateWrapper<CoreMissionAccept> uw = new UpdateWrapper<>();
		uw.lambda().eq(CoreMissionAccept::getMissionId, missionId)
				.and(wrapper -> wrapper.eq(CoreMissionAccept::getProceedPropertyId, MissionProceedType.WAIT_COMMIT)
						.or().eq(CoreMissionAccept::getProceedPropertyId, MissionProceedType.IN_REVIEW)
						.or().eq(CoreMissionAccept::getProceedPropertyId, MissionProceedType.REJECTED)
				).set(CoreMissionAccept::getProceedPropertyId, MissionProceedType.BAN)
				.set(CoreMissionAccept::getFinishTime, LocalDateTime.now());
		this.update(uw);
	}
}
