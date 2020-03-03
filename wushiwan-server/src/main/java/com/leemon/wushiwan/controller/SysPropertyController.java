package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.SysProperty;
import com.leemon.wushiwan.service.ISysPropertyService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 属性管理
 * @author: leemon
 * @date: 2019-05-06
 */
@RestController
@RequestMapping("/sys-property")
@Slf4j
public class SysPropertyController extends BaseController<SysProperty> {
	private ISysPropertyService sysPropertyService;

	@Autowired
	public SysPropertyController(ISysPropertyService sysPropertyService) {
		this.sysPropertyService = sysPropertyService;
	}

	@PreAuthorize("hasPermission('sys_property') || isClientUser()")
	@RequestMapping(value = "/properties")
	public Object queryProperties() {
		return sysPropertyService.list();
	}

	/**
	 * 分页列表查询
	 *
	 * @param sysProperty
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_property')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid SysProperty sysProperty) {
		Page<SysProperty> page = new Page<>(sysProperty.getPageNo(), sysProperty.getPageSize());
		return sysPropertyService.page(page, getLikeQueryWrapper(sysProperty));
	}

	/**
	 * 保存
	 *
	 * @param sysProperty
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_property_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid SysProperty sysProperty) {
		if (sysProperty.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		sysPropertyService.saveOrUpdate(sysProperty);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param sysProperty
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_property_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid SysProperty sysProperty) {
		if (sysProperty.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = sysPropertyService.updateById(sysProperty);
		if (!ok) {
			log.error("sysProperty = {}", sysProperty);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysProperty失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_property_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		SysProperty sysProperty = getExistEntityById(idRequest.getId(), sysPropertyService);
		boolean ok = sysPropertyService.removeById(sysProperty);
		if (!ok) {
			log.error("sysProperty = {}", sysProperty);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysProperty失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_property_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		sysPropertyService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_property')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return sysPropertyService.getById(idRequest.getId());
	}

}
