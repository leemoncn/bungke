package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.enums.MissionPublishType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreMissionDetail;
import com.leemon.wushiwan.mapper.CoreMissionDetailMapper;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 任务详情
 * @author: leemon
 * @date: 2019-05-19
 */
@RestController
@RequestMapping("/core-mission-detail")
@Slf4j
public class CoreMissionDetailController extends BaseController<CoreMissionDetail> {

	private final ICoreMissionDetailService coreMissionDetailService;
	private final CoreMissionDetailMapper detailMapper;

	@Autowired
	public CoreMissionDetailController(ICoreMissionDetailService coreMissionDetailService, CoreMissionDetailMapper detailMapper) {
		this.coreMissionDetailService = coreMissionDetailService;
		this.detailMapper = detailMapper;
	}

	@Data
	private static class ModifyRequest {
		@NotNull
		private Integer missionId;
		@NotNull
		@Min(1)
		private Integer number;
	}

	private MissionDetail getExistPublishedMissionDetail(Integer missionId) {
		MissionDetail missionDetail = coreMissionDetailService.getMissionDetailByMissionId(missionId);
		if (missionDetail == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "任务ID不存在");
		}
		if (missionDetail.getStatusPropertyId() != MissionPublishType.PUBLISHED) {
			throw new LogicException("只有发布状态的任务才可以进行调整");
		}
		return missionDetail;
	}

	/**
	 * 追加任务数量
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/add-count")
	public Object addMissionCount(@Valid @RequestBody ModifyRequest req) {
		MissionDetail missionDetail = getExistPublishedMissionDetail(req.getMissionId());
		SysUser user = UserUtil.getSysUser();
		coreMissionDetailService.addMissionCount(user, missionDetail, req.getNumber());
		return user;
	}

	/**
	 * 上调任务出价
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/add-price")
	public Object addMissionPrice(@Valid @RequestBody ModifyRequest req) {
		MissionDetail missionDetail = getExistPublishedMissionDetail(req.getMissionId());
		if (missionDetail.getPrice() >= req.getNumber()) {
			throw new LogicException("任务价格只能上调");
		}
		SysUser user = UserUtil.getSysUser();
		coreMissionDetailService.addMissionPrice(user, missionDetail, req.getNumber());
		return user;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreMissionDetail
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_detail')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreMissionDetail coreMissionDetail) {
		Page<CoreMissionDetail> page = new Page<>(coreMissionDetail.getPageNo(), coreMissionDetail.getPageSize());
		return coreMissionDetailService.page(page, getLikeQueryWrapper(coreMissionDetail));
	}

	/**
	 * 保存
	 *
	 * @param coreMissionDetail
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_detail_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreMissionDetail coreMissionDetail) {
		if (coreMissionDetail.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreMissionDetailService.saveOrUpdate(coreMissionDetail);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreMissionDetail
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_detail_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreMissionDetail coreMissionDetail) {
		if (coreMissionDetail.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreMissionDetailService.updateById(coreMissionDetail);
		if (!ok) {
			log.error("coreMissionDetail = {}", coreMissionDetail);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionDetail失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_detail_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreMissionDetail coreMissionDetail = getExistEntityById(idRequest.getId(), coreMissionDetailService);
		boolean ok = coreMissionDetailService.removeById(coreMissionDetail);
		if (!ok) {
			log.error("coreMissionDetail = {}", coreMissionDetail);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionDetail失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_detail_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreMissionDetailService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_detail')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreMissionDetailService.getById(idRequest.getId());
	}

}
