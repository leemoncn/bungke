package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.entity.CorePartnerPurchase;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreCurrencyChange;
import com.leemon.wushiwan.service.ICoreCurrencyChangeService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 货币变动
 * @author: leemon
 * @date: 2019-06-16
 */
@RestController
@RequestMapping("/core-currency-change")
@Slf4j
public class CoreCurrencyChangeController extends BaseController<CoreCurrencyChange> {

	private final ICoreCurrencyChangeService coreCurrencyChangeService;

	@Autowired
	public CoreCurrencyChangeController(ICoreCurrencyChangeService coreCurrencyChangeService) {
		this.coreCurrencyChangeService = coreCurrencyChangeService;
	}

	@RequestMapping(value = "/clist")
	public Object clist(@RequestBody @Valid CoreCurrencyChange coreCurrencyChange) {
		Page<CoreCurrencyChange> page = new Page<>(coreCurrencyChange.getPageNo(), coreCurrencyChange.getPageSize());
		page.setDesc("create_time");
		QueryWrapper<CoreCurrencyChange> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreCurrencyChange::getUserId, UserUtil.getSysUserId());
		return coreCurrencyChangeService.page(page, qw);
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreCurrencyChange
	 * @return
	 */
	@PreAuthorize("hasPermission('core_currency_change')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreCurrencyChange coreCurrencyChange) {
		Page<CoreCurrencyChange> page = new Page<>(coreCurrencyChange.getPageNo(), coreCurrencyChange.getPageSize());
		page.setDesc("create_time");
		return coreCurrencyChangeService.page(page, getLikeQueryWrapper(coreCurrencyChange));
	}

	/**
	 * 保存
	 *
	 * @param coreCurrencyChange
	 * @return
	 */
	@PreAuthorize("hasPermission('core_currency_change_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreCurrencyChange coreCurrencyChange) {
		if (coreCurrencyChange.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreCurrencyChangeService.saveOrUpdate(coreCurrencyChange);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreCurrencyChange
	 * @return
	 */
	@PreAuthorize("hasPermission('core_currency_change_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreCurrencyChange coreCurrencyChange) {
		if (coreCurrencyChange.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreCurrencyChangeService.updateById(coreCurrencyChange);
		if (!ok) {
			log.error("coreCurrencyChange = {}", coreCurrencyChange);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreCurrencyChange失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_currency_change_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreCurrencyChange coreCurrencyChange = getExistEntityById(idRequest.getId(), coreCurrencyChangeService);
		boolean ok = coreCurrencyChangeService.removeById(coreCurrencyChange);
		if (!ok) {
			log.error("coreCurrencyChange = {}", coreCurrencyChange);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreCurrencyChange失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_currency_change_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreCurrencyChangeService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_currency_change')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreCurrencyChangeService.getById(idRequest.getId());
	}

}
