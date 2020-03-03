package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.FinancialTrade;
import com.leemon.wushiwan.service.IFinancialTradeService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 支付记录
 * @author: leemon
 * @date: 2019-07-14
 */
@RestController
@RequestMapping("/financial-trade")
@Slf4j
public class FinancialTradeController extends BaseController<FinancialTrade> {

	private final IFinancialTradeService financialTradeService;

	@Autowired
	public FinancialTradeController(IFinancialTradeService financialTradeService) {
		this.financialTradeService = financialTradeService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param financialTrade
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_trade')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid FinancialTrade financialTrade) {
		Page<FinancialTrade> page = new Page<>(financialTrade.getPageNo(), financialTrade.getPageSize());
		return financialTradeService.page(page, getLikeQueryWrapper(financialTrade));
	}

	/**
	 * 保存
	 *
	 * @param financialTrade
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_trade_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid FinancialTrade financialTrade) {
		if (financialTrade.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		financialTradeService.saveOrUpdate(financialTrade);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param financialTrade
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_trade_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid FinancialTrade financialTrade) {
		if (financialTrade.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = financialTradeService.updateById(financialTrade);
		if (!ok) {
			log.error("financialTrade = {}", financialTrade);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新FinancialTrade失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_trade_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		FinancialTrade financialTrade = getExistEntityById(idRequest.getId(), financialTradeService);
		boolean ok = financialTradeService.removeById(financialTrade);
		if (!ok) {
			log.error("financialTrade = {}", financialTrade);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新FinancialTrade失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_trade_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		financialTradeService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('financial_trade')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return financialTradeService.getById(idRequest.getId());
	}

}
