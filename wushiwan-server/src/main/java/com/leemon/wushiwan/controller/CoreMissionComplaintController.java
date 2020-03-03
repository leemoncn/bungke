package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMissionAccept;
import com.leemon.wushiwan.enums.ChatStatusType;
import com.leemon.wushiwan.enums.MissionProceedType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreMissionComplaint;
import com.leemon.wushiwan.service.ICoreMissionAcceptService;
import com.leemon.wushiwan.service.ICoreMissionComplaintService;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import com.leemon.wushiwan.service.ISocialReviewChatService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 任务投诉
 * @author: leemon
 * @date: 2019-06-14
 */
@RestController
@RequestMapping("/core-mission-complaint")
@Slf4j
public class CoreMissionComplaintController extends BaseController<CoreMissionComplaint> {

	private final ICoreMissionComplaintService coreMissionComplaintService;
	private final ICoreMissionAcceptService coreMissionAcceptService;
	private final ISocialReviewChatService chatService;
	private final ICoreMissionDetailService detailService;

	@Autowired
	public CoreMissionComplaintController(ICoreMissionComplaintService coreMissionComplaintService, ICoreMissionAcceptService coreMissionAcceptService, ISocialReviewChatService chatService, ICoreMissionDetailService detailService) {
		this.coreMissionComplaintService = coreMissionComplaintService;
		this.coreMissionAcceptService = coreMissionAcceptService;
		this.chatService = chatService;
		this.detailService = detailService;
	}

	@Data
	private static class ComplaintRequest {
		@NotNull
		@Min(1)
		private Integer acceptId;
		@NotBlank
		@Length(max = 500)
		private String text;
	}

	/**
	 * 用户对不合格的任务提交申诉
	 *
	 * @param req
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/complaint")
	public Object complaint(@RequestBody @Valid ComplaintRequest req) {
		CoreMissionAccept cma = coreMissionAcceptService.getById(req.acceptId);
		if (cma == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "acceptId[%d]不存在", req.getAcceptId());
		}
		if (cma.getProceedPropertyId() != MissionProceedType.REJECTED) {
			throw new LogicException("只有不合格的任务才可以申诉");
		}
		MissionDetail md = detailService.getMissionDetailByMissionId(cma.getMissionId());
		CoreMissionComplaint cmc = new CoreMissionComplaint();
		cmc.setMissionAcceptId(req.getAcceptId());
		cmc.setComplainterUserId(UserUtil.getSysUserId());
		cmc.setText(req.getText());
		coreMissionComplaintService.save(cmc);
		chatService.addNewChat(req.getText(), md, null, UserUtil.getSysUserId(), md.getUserId(), req.acceptId, ChatStatusType.Complain.getTitle());
		return null;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreMissionComplaint
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_complaint')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreMissionComplaint coreMissionComplaint) {
		Page<CoreMissionComplaint> page = new Page<>(coreMissionComplaint.getPageNo(), coreMissionComplaint.getPageSize());
		return coreMissionComplaintService.page(page, getLikeQueryWrapper(coreMissionComplaint));
	}

	/**
	 * 保存
	 *
	 * @param coreMissionComplaint
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_complaint_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreMissionComplaint coreMissionComplaint) {
		if (coreMissionComplaint.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreMissionComplaintService.saveOrUpdate(coreMissionComplaint);
		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_complaint_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreMissionComplaint coreMissionComplaint = getExistEntityById(idRequest.getId(), coreMissionComplaintService);
		boolean ok = coreMissionComplaintService.removeById(coreMissionComplaint);
		if (!ok) {
			log.error("coreMissionComplaint = {}", coreMissionComplaint);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionComplaint失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_complaint_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreMissionComplaintService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_complaint')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreMissionComplaintService.getById(idRequest.getId());
	}

	@Data
	private static class HandleComplaintRequest {
		@NotNull
		@Min(1)
		private Integer id;
		@NotNull
		private Boolean result;
	}

	/**
	 * 处理申诉
	 *
	 * @param req
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@PreAuthorize("hasPermission('core_mission_complaint_edit')")
	@RequestMapping(value = "/handle-complaint")
	public Object handleComplaint(@RequestBody @Valid HandleComplaintRequest req) {
		CoreMissionComplaint cmc = getExistEntityById(req.getId(), coreMissionComplaintService);
		if (cmc.getResult() != null) {
			throw new LogicException("任务申诉只能处理一次，无法更改结果");
		}
		cmc.setResult(req.getResult());
		coreMissionComplaintService.updateById(cmc);
		coreMissionAcceptService.reviewMission(cmc.getMissionAcceptId(), req.getResult(), null, null, true, UserUtil.getSysUser().getNickname());
		return null;
	}

}
