package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.enums.UserType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreMissionRule;
import com.leemon.wushiwan.service.ICoreMissionRuleService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 任务规则管理
 * @author: leemon
 * @date: 2019-05-09
 */
@RestController
@RequestMapping("/core-mission-rule")
@Slf4j
public class CoreMissionRuleController extends BaseController<CoreMissionRule> {

	private final ICoreMissionRuleService coreMissionRuleService;

	@Autowired
	public CoreMissionRuleController(ICoreMissionRuleService coreMissionRuleService) {
		this.coreMissionRuleService = coreMissionRuleService;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreMissionRule
	 * @return
	 */
	@PreAuthorize("isClientUser() || hasPermission('core_mission_rule')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreMissionRule coreMissionRule) {
		if (UserUtil.getSysUser().getUserType() == UserType.CLIENT) {
			QueryWrapper<CoreMissionRule> qw = new QueryWrapper<>();
			qw.lambda().eq(CoreMissionRule::getUsable, 1);
			return coreMissionRuleService.list(qw);
		}
		Page<CoreMissionRule> page = new Page<>(coreMissionRule.getPageNo(), coreMissionRule.getPageSize());
		return coreMissionRuleService.page(page, getLikeQueryWrapper(coreMissionRule));
	}

	/**
	 * 保存
	 *
	 * @param coreMissionRule
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_rule_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreMissionRule coreMissionRule) {
		if (coreMissionRule.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreMissionRuleService.saveOrUpdate(coreMissionRule);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreMissionRule
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_rule_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreMissionRule coreMissionRule) {
		if (coreMissionRule.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreMissionRuleService.updateById(coreMissionRule);
		if (!ok) {
			log.error("coreMissionRule = {}", coreMissionRule);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionRule失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_rule_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreMissionRule coreMissionRule = getExistEntityById(idRequest.getId(), coreMissionRuleService);
		boolean ok = coreMissionRuleService.removeById(coreMissionRule);
		if (!ok) {
			log.error("coreMissionRule = {}", coreMissionRule);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMissionRule失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_rule_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreMissionRuleService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_rule')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreMissionRuleService.getById(idRequest.getId());
	}

}
