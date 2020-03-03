package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSON;
import com.leemon.wushiwan.dto.PartnerInfo;
import com.leemon.wushiwan.dto.PayPartner;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import com.leemon.wushiwan.enums.PartnerTimeType;
import com.leemon.wushiwan.enums.PayType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CorePartnerPurchase;
import com.leemon.wushiwan.mapper.CorePartnerPurchaseMapper;
import com.leemon.wushiwan.service.ICoreCurrencyChangeService;
import com.leemon.wushiwan.service.ICorePartnerPurchaseService;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.service.IFinancialTradeService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 合作商购买记录
 * @author: leemon
 * @date: 2019-06-27
 */
@RestController
@RequestMapping("/core-partner-purchase")
@Slf4j
public class CorePartnerPurchaseController extends BaseController<CorePartnerPurchase> {

	private final ICorePartnerPurchaseService corePartnerPurchaseService;
	private final ICorePartnerService partnerService;
	private final IFinancialTradeService tradeService;
	private final CorePartnerPurchaseMapper corePartnerPurchaseMapper;
	private final ISysUserService userService;
	private final ICoreCurrencyChangeService currencyChangeService;

	@Autowired
	public CorePartnerPurchaseController(ICorePartnerPurchaseService corePartnerPurchaseService, ICorePartnerService partnerService, IFinancialTradeService tradeService, CorePartnerPurchaseMapper corePartnerPurchaseMapper, ISysUserService userService, ICoreCurrencyChangeService currencyChangeService) {
		this.corePartnerPurchaseService = corePartnerPurchaseService;
		this.partnerService = partnerService;
		this.tradeService = tradeService;
		this.corePartnerPurchaseMapper = corePartnerPurchaseMapper;
		this.userService = userService;
		this.currencyChangeService = currencyChangeService;
	}

	@Data
	private static class BuyPartnerRequest {
		@NotNull
		private Boolean isYear;
		@NotNull
		private Integer partnerId;
	}

	@PostMapping("/buy")
	public Object buyPartner(@RequestBody @Valid BuyPartnerRequest req, @RequestHeader(value = "isApp") Boolean isApp) {
		SysUser user = UserUtil.getSysUser();
		PartnerTimeType type = req.getIsYear() ? PartnerTimeType.YEAH : PartnerTimeType.MONTH;
		//先看看用户当前是否是合作商
		PartnerInfo userCurrentPartner = corePartnerPurchaseMapper.selectValidPartnerInfoByUser(user.getId());
		//想要购买的合作商
		CorePartner buyPartner = partnerService.getById(req.getPartnerId());

		if (buyPartner == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID);
		}
		Integer amount = req.getIsYear() ? buyPartner.getYearPrice() : buyPartner.getMouthPrice();
		if (buyPartner.getLevel() == 0) {
			throw new LogicException(ErrorCode.SYS_ERROR, "普通合作商无需购买");
		}
		if (userCurrentPartner != null) {//当前是合作商
			if (buyPartner.getLevel() < userCurrentPartner.getLevel()) {
				throw new LogicException("合作商只可升级，不能降级");
			}
		}
		PayPartner pp = new PayPartner().setIsYear(req.getIsYear()).setPartnerId(req.getPartnerId());
		return tradeService.startPay(user.getId(), amount, PayType.Partner, JSON.toJSONString(pp), isApp);
	}

	/*----------------------------以下是admin的接口----------------------------**/

	@EqualsAndHashCode(callSuper = true)
	@Data
	private static class SendPartnerRequest extends BuyPartnerRequest {
		@NotNull
		private Integer userId;
	}

	/**
	 * 后台管理员向用户赠送合作商
	 *
	 * @param req
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_purchase_add')")
	@PostMapping("/send")
	public Object sendPartner(@RequestBody @Valid SendPartnerRequest req) {
		SysUser toUser = userService.getById(req.getUserId());
		if (toUser == null) {
			throw new LogicException("用户ID不存在");
		}
		PartnerTimeType type = req.getIsYear() ? PartnerTimeType.YEAH : PartnerTimeType.MONTH;
		//先看看用户当前是否是合作商
		PartnerInfo userCurrentPartner = corePartnerPurchaseMapper.selectValidPartnerInfoByUser(toUser.getId());
		//想要购买的合作商
		CorePartner buyPartner = partnerService.getById(req.getPartnerId());

		if (buyPartner == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID);
		}
		Integer amount = req.getIsYear() ? buyPartner.getYearPrice() : buyPartner.getMouthPrice();
		if (buyPartner.getLevel() == 0) {
			throw new LogicException(ErrorCode.SYS_ERROR, "普通合作商无需购买");
		}
		if (userCurrentPartner != null) {//当前是合作商
			if (buyPartner.getLevel() < userCurrentPartner.getLevel()) {
				throw new LogicException("合作商只可升级，不能降级");
			}
		}
		PayPartner pp = new PayPartner().setIsYear(req.getIsYear()).setPartnerId(req.getPartnerId());
		corePartnerPurchaseService.addNewPartner(toUser, pp.getPartnerId(), type, "system");
		return null;
	}

	/**
	 * 分页列表查询
	 *
	 * @param corePartnerPurchase
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_purchase')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CorePartnerPurchase corePartnerPurchase) {
		Page<CorePartnerPurchase> page = new Page<>(corePartnerPurchase.getPageNo(), corePartnerPurchase.getPageSize());
		QueryWrapper<CorePartnerPurchase> qw = getLikeQueryWrapper(corePartnerPurchase);
		return corePartnerPurchaseService.page(page, qw);
	}

	/**
	 * 保存
	 *
	 * @param corePartnerPurchase
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_purchase_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CorePartnerPurchase corePartnerPurchase) {
		if (corePartnerPurchase.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		corePartnerPurchaseService.saveOrUpdate(corePartnerPurchase);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param corePartnerPurchase
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_purchase_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CorePartnerPurchase corePartnerPurchase) {
		if (corePartnerPurchase.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = corePartnerPurchaseService.updateById(corePartnerPurchase);
		if (!ok) {
			log.error("corePartnerPurchase = {}", corePartnerPurchase);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CorePartnerPurchase失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_purchase_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CorePartnerPurchase corePartnerPurchase = getExistEntityById(idRequest.getId(), corePartnerPurchaseService);
		boolean ok = corePartnerPurchaseService.removeById(corePartnerPurchase);
		if (!ok) {
			log.error("corePartnerPurchase = {}", corePartnerPurchase);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CorePartnerPurchase失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_purchase_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		corePartnerPurchaseService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_purchase')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return corePartnerPurchaseService.getById(idRequest.getId());
	}

}
