package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.enums.MissionPublishType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreTop;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import com.leemon.wushiwan.service.ICoreTopService;
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
 * @Description: 任务置顶记录
 * @author: leemon
 * @date: 2019-07-25
 */
@RestController
@RequestMapping("/core-top")
@Slf4j
public class CoreTopController extends BaseController<CoreTop> {

	private final ICoreTopService coreTopService;
	private final ICoreMissionDetailService detailService;

	@Autowired
	public CoreTopController(ICoreTopService coreTopService, ICoreMissionDetailService detailService) {
		this.coreTopService = coreTopService;
		this.detailService = detailService;
	}

	@Data
	private static class TopMissionRequest {
		@NotNull
		Integer missionId;
		@NotNull
		@Min(1)
		Integer hours;
	}

	@RequestMapping("/top-mission")
	public Object topMission(@Valid @RequestBody TopMissionRequest req) {
		MissionDetail md = detailService.getMissionDetailByMissionId(req.getMissionId());
		if (md == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "任务不存在");
		}
		if (md.getStatusPropertyId() != MissionPublishType.PUBLISHED) {
			throw new LogicException("只有已发布的任务可以置顶");
		}
		coreTopService.addTopMission(req.getMissionId(), req.getHours(), md);
		return UserUtil.getSysUser();
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreTop
	 * @return
	 */
	@PreAuthorize("hasPermission('core_top')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreTop coreTop) {
		Page<CoreTop> page = new Page<>(coreTop.getPageNo(), coreTop.getPageSize());
		return coreTopService.page(page, getLikeQueryWrapper(coreTop));
	}

	/**
	 * 保存
	 *
	 * @param coreTop
	 * @return
	 */
	@PreAuthorize("hasPermission('core_top_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreTop coreTop) {
		if (coreTop.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreTopService.saveOrUpdate(coreTop);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreTop
	 * @return
	 */
	@PreAuthorize("hasPermission('core_top_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreTop coreTop) {
		if (coreTop.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreTopService.updateById(coreTop);
		if (!ok) {
			log.error("coreTop = {}", coreTop);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreTop失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_top_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreTop coreTop = getExistEntityById(idRequest.getId(), coreTopService);
		boolean ok = coreTopService.removeById(coreTop);
		if (!ok) {
			log.error("coreTop = {}", coreTop);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreTop失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_top_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreTopService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_top')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreTopService.getById(idRequest.getId());
	}

}
