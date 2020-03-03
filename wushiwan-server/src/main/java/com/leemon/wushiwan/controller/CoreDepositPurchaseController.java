package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.PayType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreDepositPurchase;
import com.leemon.wushiwan.service.ICoreDepositPurchaseService;
import com.leemon.wushiwan.service.IFinancialTradeService;
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
 * @Description: 保证金记录
 * @author: leemon
 * @date: 2019-07-01
 */
@RestController
@RequestMapping("/core-deposit-purchase")
@Slf4j
public class CoreDepositPurchaseController extends BaseController<CoreDepositPurchase> {

	private final ICoreDepositPurchaseService coreDepositPurchaseService;
	private final IFinancialTradeService tradeService;

	@Autowired
	public CoreDepositPurchaseController(ICoreDepositPurchaseService coreDepositPurchaseService, IFinancialTradeService tradeService) {
		this.coreDepositPurchaseService = coreDepositPurchaseService;
		this.tradeService = tradeService;
	}

	@Data
	private static class RechargeDepositRequest {
		@NotNull
		@Min(100000)
		private Integer amount;
	}

	@RequestMapping("/recharge")
	public Object rechange(@RequestBody @Valid RechargeDepositRequest req, @RequestHeader(value = "isApp") Boolean isApp) {
		Integer amount = req.getAmount();
		return tradeService.startPay(UserUtil.getSysUserId(), amount, PayType.RechargeDeposit, "", isApp);
	}

	@RequestMapping("/withdrawal")
	public Object withdrawal() {
		coreDepositPurchaseService.refundDeposit(UserUtil.getSysUser());
		return UserUtil.getSysUser();
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreDepositPurchase
	 * @return
	 */
	@PreAuthorize("hasPermission('core_deposit_purchase')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreDepositPurchase coreDepositPurchase) {
		Page<CoreDepositPurchase> page = new Page<>(coreDepositPurchase.getPageNo(), coreDepositPurchase.getPageSize());
		return coreDepositPurchaseService.page(page, getLikeQueryWrapper(coreDepositPurchase));
	}

	/**
	 * 保存
	 *
	 * @param coreDepositPurchase
	 * @return
	 */
	@PreAuthorize("hasPermission('core_deposit_purchase_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreDepositPurchase coreDepositPurchase) {
		if (coreDepositPurchase.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreDepositPurchaseService.saveOrUpdate(coreDepositPurchase);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreDepositPurchase
	 * @return
	 */
	@PreAuthorize("hasPermission('core_deposit_purchase_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreDepositPurchase coreDepositPurchase) {
		if (coreDepositPurchase.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreDepositPurchaseService.updateById(coreDepositPurchase);
		if (!ok) {
			log.error("coreDepositPurchase = {}", coreDepositPurchase);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreDepositPurchase失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_deposit_purchase_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreDepositPurchase coreDepositPurchase = getExistEntityById(idRequest.getId(), coreDepositPurchaseService);
		boolean ok = coreDepositPurchaseService.removeById(coreDepositPurchase);
		if (!ok) {
			log.error("coreDepositPurchase = {}", coreDepositPurchase);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreDepositPurchase失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_deposit_purchase_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreDepositPurchaseService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_deposit_purchase')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreDepositPurchaseService.getById(idRequest.getId());
	}

}
