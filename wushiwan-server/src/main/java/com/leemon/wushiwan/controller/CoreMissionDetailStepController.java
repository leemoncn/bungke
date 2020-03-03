package com.leemon.wushiwan.controller;

import javax.validation.Valid;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMissionDetailStep;
import com.leemon.wushiwan.enums.CoreImgTypeMission;
import com.leemon.wushiwan.mapper.CoreMissionDetailStepMapper;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import com.leemon.wushiwan.service.ICoreMissionDetailStepService;
import com.leemon.wushiwan.vo.IdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 任务执行步骤
 * @author: leemon
 * @date: 2019-08-15
 */
@RestController
@RequestMapping("/core-mission-detail-step")
@Slf4j
public class CoreMissionDetailStepController extends BaseController<CoreMissionDetailStep> {

	private final ICoreMissionDetailStepService coreMissionDetailStepService;
	private final CoreMissionDetailStepMapper stepMapper;
	private final ICoreMissionDetailService detailService;

	@Autowired
	public CoreMissionDetailStepController(ICoreMissionDetailStepService coreMissionDetailStepService, CoreMissionDetailStepMapper stepMapper, ICoreMissionDetailService detailService) {
		this.coreMissionDetailStepService = coreMissionDetailStepService;
		this.stepMapper = stepMapper;
		this.detailService = detailService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param req
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission') || isClientUser()")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid IdRequest req) {
		MissionDetail md = detailService.getMissionDetailByMissionId(req.getId());
		if (md == null) {
			return null;
		}
		return stepMapper.selectExplainData(md.getUserId(), CoreImgTypeMission.EXPLAIN.ordinal(), md.getId());
	}

//	/**
//	 * 保存
//	 *
//	 * @param coreMissionDetailStep
//	 * @return
//	 */
//	@PreAuthorize("hasPermission('core_mission_detail_step_add')")
//	@RequestMapping(value = "/add")
//	public Object add(@RequestBody @Valid CoreMissionDetailStep coreMissionDetailStep) {
//		if (coreMissionDetailStep.getId() != null) {
//			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
//		}
//		coreMissionDetailStepService.saveOrUpdate(coreMissionDetailStep);
//		return null;
//	}
//
//	/**
//	 * 编辑
//	 *
//	 * @param coreMissionDetailStep
//	 * @return
//	 */
//	@PreAuthorize("hasPermission('core_mission_detail_step_edit')")
//	@RequestMapping(value = "/edit")
//	public Object edit(@RequestBody @Valid CoreMissionDetailStep coreMissionDetailStep) {
//		if (coreMissionDetailStep.getId() == null) {
//			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
//		}
//		boolean ok = coreMissionDetailStepService.updateById(coreMissionDetailStep);
//		if (!ok) {
//			log.error("coreMissionDetailStep = {}", coreMissionDetailStep);
//			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionDetailStep失败");
//		}
//
//		return null;
//	}
//
//	/**
//	 * 通过id删除
//	 *
//	 * @param idRequest
//	 * @return
//	 */
//	@PreAuthorize("hasPermission('core_mission_detail_step_delete')")
//	@RequestMapping(value = "/delete")
//	public Object delete(@RequestBody @Valid IdRequest idRequest) {
//		CoreMissionDetailStep coreMissionDetailStep = getExistEntityById(idRequest.getId(), coreMissionDetailStepService);
//		boolean ok = coreMissionDetailStepService.removeById(coreMissionDetailStep);
//		if (!ok) {
//			log.error("coreMissionDetailStep = {}", coreMissionDetailStep);
//			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionDetailStep失败");
//		}
//
//		return null;
//	}
//
//	/**
//	 * 批量删除
//	 *
//	 * @param idsRequest
//	 * @return
//	 */
//	@PreAuthorize("hasPermission('core_mission_detail_step_delete')")
//	@RequestMapping(value = "/deleteBatch")
//	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
//		coreMissionDetailStepService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
//		return null;
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param idRequest
//	 * @return
//	 */
//	@PreAuthorize("hasPermission('core_mission_detail_step')")
//	@RequestMapping(value = "/queryById")
//	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
//		return coreMissionDetailStepService.getById(idRequest.getId());
//	}

}
