package com.leemon.wushiwan.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CoreQuestion;
import com.leemon.wushiwan.service.ICoreQuestionService;
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
 * @Description: 常见问题
 * @author: leemon
 * @date: 2019-10-27
 */
@RestController
@RequestMapping("/core-question")
@Slf4j
public class CoreQuestionController extends BaseController<CoreQuestion> {

	private final ICoreQuestionService coreQuestionService;

	@Autowired
	public CoreQuestionController(ICoreQuestionService coreQuestionService) {
		this.coreQuestionService = coreQuestionService;
	}

	@RequestMapping(value = "/clist")
	public Object clist() {
		return coreQuestionService.list();
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreQuestion
	 * @return
	 */
	@PreAuthorize("hasPermission('core_question')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreQuestion coreQuestion) {
		Page<CoreQuestion> page = new Page<>(coreQuestion.getPageNo(), coreQuestion.getPageSize());
		return coreQuestionService.page(page, getLikeQueryWrapper(coreQuestion));
	}

	/**
	 * 保存
	 *
	 * @param coreQuestion
	 * @return
	 */
	@PreAuthorize("hasPermission('core_question_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreQuestion coreQuestion) {
		if (coreQuestion.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreQuestionService.saveOrUpdate(coreQuestion);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param coreQuestion
	 * @return
	 */
	@PreAuthorize("hasPermission('core_question_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreQuestion coreQuestion) {
		if (coreQuestion.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = coreQuestionService.updateById(coreQuestion);
		if (!ok) {
			log.error("coreQuestion = {}", coreQuestion);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreQuestion失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_question_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreQuestion coreQuestion = getExistEntityById(idRequest.getId(), coreQuestionService);
		boolean ok = coreQuestionService.removeById(coreQuestion);
		if (!ok) {
			log.error("coreQuestion = {}", coreQuestion);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreQuestion失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_question_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreQuestionService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_question')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreQuestionService.getById(idRequest.getId());
	}

}
