package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.FinancialWithdrawal;
import com.leemon.wushiwan.service.IFinancialWithdrawalService;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 提现申请
 * @author: leemon
 * @date: 2019-06-29
 */
@RestController
@RequestMapping("/financial-withdrawal")
@Slf4j
public class FinancialWithdrawalController extends BaseController<FinancialWithdrawal> {

	private final IFinancialWithdrawalService financialWithdrawalService;
	private final ISocialNoticeService noticeService;

	@Autowired
	public FinancialWithdrawalController(IFinancialWithdrawalService financialWithdrawalService, ISocialNoticeService noticeService) {
		this.financialWithdrawalService = financialWithdrawalService;
		this.noticeService = noticeService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param financialWithdrawal
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_withdrawal')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid FinancialWithdrawal financialWithdrawal) {
		Page<FinancialWithdrawal> page = new Page<>(financialWithdrawal.getPageNo(), financialWithdrawal.getPageSize());
		page.setDesc("create_time");
		return financialWithdrawalService.page(page, getLikeQueryWrapper(financialWithdrawal));
	}

	/**
	 * 保存
	 *
	 * @param financialWithdrawal
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_withdrawal_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid FinancialWithdrawal financialWithdrawal) {
		if (financialWithdrawal.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		financialWithdrawalService.saveOrUpdate(financialWithdrawal);
		return null;
	}

	/**
	 * 审核提现申请
	 *
	 * @param financialWithdrawal
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_withdrawal_edit')")
	@RequestMapping(value = "/review-withdrawal")
	public Object edit(@RequestBody @Valid FinancialWithdrawal financialWithdrawal) {
		if (financialWithdrawal.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "需要传入id");
		}
		if (financialWithdrawal.getApprove() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "需要传入审核结果");
		}
		financialWithdrawalService.approveWithdrawal(financialWithdrawal.getId(), financialWithdrawal.getApprove(), UserUtil.getSysUser());
		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_withdrawal_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		FinancialWithdrawal financialWithdrawal = getExistEntityById(idRequest.getId(), financialWithdrawalService);
		boolean ok = financialWithdrawalService.removeById(financialWithdrawal);
		if (!ok) {
			log.error("financialWithdrawal = {}", financialWithdrawal);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新FinancialWithdrawal失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_withdrawal_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		financialWithdrawalService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_withdrawal')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return financialWithdrawalService.getById(idRequest.getId());
	}

}
