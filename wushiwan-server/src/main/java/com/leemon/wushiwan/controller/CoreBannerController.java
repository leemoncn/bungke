package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.common.base.Strings;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreBanner;
import com.leemon.wushiwan.service.ICoreBannerService;
import com.leemon.wushiwan.service.QiniuKodoService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: banner管理
 * @author: leemon
 * @date: 2019-11-18
 */
@RestController
@RequestMapping("/core-banner")
@Slf4j
public class CoreBannerController extends BaseController<CoreBanner> {

	private final ICoreBannerService coreBannerService;
	private final QiniuKodoService kodoService;

	@Autowired
	public CoreBannerController(ICoreBannerService coreBannerService, QiniuKodoService kodoService) {
		this.coreBannerService = coreBannerService;
		this.kodoService = kodoService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreBanner
	 * @return
	 */
	@PreAuthorize("hasPermission('core_banner')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreBanner coreBanner, HttpServletRequest request) {
		Page<CoreBanner> page = new Page<>(coreBanner.getPageNo(), coreBanner.getPageSize());
		page.setAsc("sort");
		return coreBannerService.page(page, getLikeQueryWrapper(coreBanner));
	}

	/**
	 * 保存
	 *
	 * @param coreBanner
	 * @return
	 */
	@PreAuthorize("hasPermission('core_banner_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreBanner coreBanner) {
		if (coreBanner.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		deleteRedisKey(coreBanner.getKey());
		coreBannerService.saveOrUpdate(coreBanner);
		return null;
	}

	private void deleteRedisKey(String key) {
		if (Strings.isNullOrEmpty(key)) {
			return;
		}
		kodoService.deleteAllFileUploadedRedisKey(key);
	}

	/**
	 * 编辑
	 *
	 * @param coreBanner
	 * @return
	 */
	@PreAuthorize("hasPermission('core_banner_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreBanner coreBanner) {
		if (coreBanner.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		deleteRedisKey(coreBanner.getKey());
		boolean ok = coreBannerService.updateById(coreBanner);
		if (!ok) {
			log.error("coreBanner = {}", coreBanner);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreBanner失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_banner_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreBanner coreBanner = getExistEntityById(idRequest.getId(), coreBannerService);
		boolean ok = coreBannerService.removeById(coreBanner);
		if (!ok) {
			log.error("coreBanner = {}", coreBanner);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreBanner失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_banner_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreBannerService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_banner')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreBannerService.getById(idRequest.getId());
	}

}
