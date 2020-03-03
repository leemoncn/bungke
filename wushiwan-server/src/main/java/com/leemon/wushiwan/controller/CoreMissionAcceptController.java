package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreMissionAccept;
import com.leemon.wushiwan.service.ICoreMissionAcceptService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 任务完成记录
 * @author: leemon
 * @date: 2019-05-19
 */
@RestController
@RequestMapping("/core-mission-accept")
@Slf4j
public class CoreMissionAcceptController extends BaseController<CoreMissionAccept> {

	private final ICoreMissionAcceptService coreMissionAcceptService;

	@Autowired
	public CoreMissionAcceptController(ICoreMissionAcceptService coreMissionAcceptService) {
		this.coreMissionAcceptService = coreMissionAcceptService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreMissionAccept
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_accept')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreMissionAccept coreMissionAccept) {
		Page<CoreMissionAccept> page = new Page<>(coreMissionAccept.getPageNo(), coreMissionAccept.getPageSize());
		return coreMissionAcceptService.page(page, getLikeQueryWrapper(coreMissionAccept));
	}

	/**
	 * 保存
	 *
	 * @param coreMissionAccept
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_accept_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreMissionAccept coreMissionAccept) {
		if (coreMissionAccept.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreMissionAcceptService.saveOrUpdate(coreMissionAccept);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreMissionAccept
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_accept_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreMissionAccept coreMissionAccept) {
		if (coreMissionAccept.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreMissionAcceptService.updateById(coreMissionAccept);
		if (!ok) {
			log.error("coreMissionAccept = {}", coreMissionAccept);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionAccept失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_accept_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreMissionAccept coreMissionAccept = getExistEntityById(idRequest.getId(), coreMissionAcceptService);
		boolean ok = coreMissionAcceptService.removeById(coreMissionAccept);
		if (!ok) {
			log.error("coreMissionAccept = {}", coreMissionAccept);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionAccept失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_accept_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreMissionAcceptService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_accept')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreMissionAcceptService.getById(idRequest.getId());
	}

}
