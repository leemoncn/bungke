package com.leemon.wushiwan.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.FinancialTransfer;
import com.leemon.wushiwan.service.IFinancialTransferService;
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
 * @Description: 支付宝转账记录
 * @author: leemon
 * @date: 2019-10-22
 */
@RestController
@RequestMapping("/financial-transfer")
@Slf4j
public class FinancialTransferController extends BaseController<FinancialTransfer> {

	private final IFinancialTransferService financialTransferService;

	@Autowired
	public FinancialTransferController(IFinancialTransferService financialTransferService) {
		this.financialTransferService = financialTransferService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param financialTransfer
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_transfer')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid FinancialTransfer financialTransfer) {
		Page<FinancialTransfer> page = new Page<>(financialTransfer.getPageNo(), financialTransfer.getPageSize());
		return financialTransferService.page(page, getLikeQueryWrapper(financialTransfer));
	}

	/**
	 * 保存
	 *
	 * @param financialTransfer
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_transfer_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid FinancialTransfer financialTransfer) {
		if (financialTransfer.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		financialTransferService.saveOrUpdate(financialTransfer);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param financialTransfer
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_transfer_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid FinancialTransfer financialTransfer) {
		if (financialTransfer.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = financialTransferService.updateById(financialTransfer);
		if (!ok) {
			log.error("financialTransfer = {}", financialTransfer);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新FinancialTransfer失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_transfer_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		FinancialTransfer financialTransfer = getExistEntityById(idRequest.getId(), financialTransferService);
		boolean ok = financialTransferService.removeById(financialTransfer);
		if (!ok) {
			log.error("financialTransfer = {}", financialTransfer);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新FinancialTransfer失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_transfer_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		financialTransferService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_transfer')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return financialTransferService.getById(idRequest.getId());
	}

}
