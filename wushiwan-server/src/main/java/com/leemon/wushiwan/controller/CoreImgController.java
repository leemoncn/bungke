package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreImg;
import com.leemon.wushiwan.service.ICoreImgService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 图片管理，管理用户上传的图片
 * @author: leemon
 * @date: 2019-04-27
 */
@RestController
@RequestMapping("/core-img")
@Slf4j
public class CoreImgController extends BaseController<CoreImg> {
	private ICoreImgService coreImgService;

	@Autowired
	public CoreImgController(ICoreImgService coreImgService) {
		this.coreImgService = coreImgService;
	}

	@Data
	private static class QueryImgRequest {
		@NotNull
		Integer dataId;
		@NotNull
		Integer type;
	}

	@PreAuthorize("hasPermission('core_img_view') || isClientUser()")
	@RequestMapping("/query")
	public Object queryImg(@Valid @RequestBody QueryImgRequest req) {
		QueryWrapper<CoreImg> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreImg::getDataId, req.getDataId())
				.eq(CoreImg::getType, req.getType());
		return coreImgService.list(qw);
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreImg
	 * @return
	 */
	@PreAuthorize("hasPermission('core_img_view')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreImg coreImg) {
		Page<CoreImg> page = new Page<>(coreImg.getPageNo(), coreImg.getPageSize());
		return coreImgService.page(page, getLikeQueryWrapper(coreImg));
	}

	/**
	 * 保存
	 *
	 * @param coreImg
	 * @return
	 */
	@PreAuthorize("hasPermission('core_img_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreImg coreImg) {
		if (coreImg.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreImgService.saveOrUpdate(coreImg);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreImg
	 * @return
	 */
	@PreAuthorize("hasPermission('core_img_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreImg coreImg) {
		if (coreImg.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreImgService.updateById(coreImg);
		if (!ok) {
			log.error("coreImg = {}", coreImg);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreImg失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_img_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreImg coreImg = getExistEntityById(idRequest.getId(), coreImgService);
		boolean ok = coreImgService.removeById(coreImg);
		if (!ok) {
			log.error("coreImg = {}", coreImg);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreImg失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_img_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreImgService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_img_view')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreImgService.getById(idRequest.getId());
	}

}
