package com.leemon.wushiwan.controller;

import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.leemon.wushiwan.enums.CoreImgTypeMission;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.SysAdvice;
import com.leemon.wushiwan.service.ICoreImgService;
import com.leemon.wushiwan.service.ISysAdviceService;
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
 * @Description: 支付记录
 * @author: leemon
 * @date: 2019-07-15
 */
@RestController
@RequestMapping("/sys-advice")
@Slf4j
public class SysAdviceController extends BaseController<SysAdvice> {

	private final ISysAdviceService sysAdviceService;
	private final ICoreImgService coreImgService;

	@Autowired
	public SysAdviceController(ISysAdviceService sysAdviceService, ICoreImgService coreImgService) {
		this.sysAdviceService = sysAdviceService;
		this.coreImgService = coreImgService;
	}

	@Data
	private static class AdviceRequest {
		@NotBlank
		private String msg;
		@NotBlank
		private String contactInfo;
		private String key;
	}

	@RequestMapping("/submit")
	public Object submit(@Valid @RequestBody AdviceRequest req) {
		sysAdviceService.addNewAdvice(req.getMsg(), req.getContactInfo(), UserUtil.getSysUserId(), req.getKey());
		return null;
	}

	/**
	 * 分页列表查询
	 *
	 * @param sysAdvice
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_advice')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid SysAdvice sysAdvice) {
		Page<SysAdvice> page = new Page<>(sysAdvice.getPageNo(), sysAdvice.getPageSize());
		return sysAdviceService.page(page, getLikeQueryWrapper(sysAdvice));
	}

	/**
	 * 保存
	 *
	 * @param sysAdvice
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_advice_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid SysAdvice sysAdvice) {
		if (sysAdvice.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		sysAdviceService.saveOrUpdate(sysAdvice);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param sysAdvice
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_advice_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid SysAdvice sysAdvice) {
		if (sysAdvice.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = sysAdviceService.updateById(sysAdvice);
		if (!ok) {
			log.error("sysAdvice = {}", sysAdvice);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysAdvice失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_advice_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		SysAdvice sysAdvice = getExistEntityById(idRequest.getId(), sysAdviceService);
		boolean ok = sysAdviceService.removeById(sysAdvice);
		if (!ok) {
			log.error("sysAdvice = {}", sysAdvice);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysAdvice失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_advice_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		sysAdviceService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_advice')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return sysAdviceService.getById(idRequest.getId());
	}

}
