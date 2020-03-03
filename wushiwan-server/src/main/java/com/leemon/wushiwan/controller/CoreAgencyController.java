package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.service.ICoreAgencyService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 抽成配置
 * @author: leemon
 * @date: 2019-06-29
 */
@RestController
@RequestMapping("/core-agency")
@Slf4j
public class CoreAgencyController extends BaseController<CoreAgency> {

	private final ICoreAgencyService coreAgencyService;

	@Autowired
	public CoreAgencyController(ICoreAgencyService coreAgencyService) {
		this.coreAgencyService = coreAgencyService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreAgency
	 * @return
	 */
	@PreAuthorize("hasPermission('core_agency')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreAgency coreAgency) {
		Page<CoreAgency> page = new Page<>(coreAgency.getPageNo(), coreAgency.getPageSize());
		return coreAgencyService.page(page, getLikeQueryWrapper(coreAgency));
	}

	/**
	 * 保存
	 *
	 * @param coreAgency
	 * @return
	 */
	@PreAuthorize("hasPermission('core_agency_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreAgency coreAgency) {
		if (coreAgency.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreAgencyService.saveOrUpdate(coreAgency);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreAgency
	 * @return
	 */
	@PreAuthorize("hasPermission('core_agency_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreAgency coreAgency) {
		if (coreAgency.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreAgencyService.updateById(coreAgency);
		if (!ok) {
			log.error("coreAgency = {}", coreAgency);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreAgency失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_agency_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreAgency coreAgency = getExistEntityById(idRequest.getId(), coreAgencyService);
		boolean ok = coreAgencyService.removeById(coreAgency);
		if (!ok) {
			log.error("coreAgency = {}", coreAgency);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreAgency失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_agency_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreAgencyService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_agency')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreAgencyService.getById(idRequest.getId());
	}

}
