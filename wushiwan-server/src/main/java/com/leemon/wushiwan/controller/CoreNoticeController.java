package com.leemon.wushiwan.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreNotice;
import com.leemon.wushiwan.service.ICoreNoticeService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.leemon.wushiwan.controller.BaseController;

/**
 * @Description: 滚动通知
 * @author: leemon
 * @date: 2019-11-18
 */
@RestController
@RequestMapping("/core-notice")
@Slf4j
public class CoreNoticeController extends BaseController<CoreNotice> {

	private final ICoreNoticeService coreNoticeService;

	@Autowired
	public CoreNoticeController(ICoreNoticeService coreNoticeService) {
		this.coreNoticeService = coreNoticeService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreNotice
	 * @return
	 */
	@PreAuthorize("hasPermission('core_notice')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreNotice coreNotice) {
		Page<CoreNotice> page = new Page<>(coreNotice.getPageNo(), coreNotice.getPageSize());
		page.setAsc("sort");
		return coreNoticeService.page(page, getLikeQueryWrapper(coreNotice));
	}

	/**
	 * 保存
	 *
	 * @param coreNotice
	 * @return
	 */
	@PreAuthorize("hasPermission('core_notice_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreNotice coreNotice) {
		if (coreNotice.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreNoticeService.saveOrUpdate(coreNotice);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreNotice
	 * @return
	 */
	@PreAuthorize("hasPermission('core_notice_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreNotice coreNotice) {
		if (coreNotice.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreNoticeService.updateById(coreNotice);
		if (!ok) {
			log.error("coreNotice = {}", coreNotice);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreNotice失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_notice_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreNotice coreNotice = getExistEntityById(idRequest.getId(), coreNoticeService);
		boolean ok = coreNoticeService.removeById(coreNotice);
		if (!ok) {
			log.error("coreNotice = {}", coreNotice);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreNotice失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_notice_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreNoticeService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_notice')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreNoticeService.getById(idRequest.getId());
	}

}
