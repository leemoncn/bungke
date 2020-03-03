package com.leemon.wushiwan.controller;

import java.util.Arrays;
import javax.validation.Valid;

import com.leemon.wushiwan.entity.BaseEntity;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.SocialNotice;
import com.leemon.wushiwan.mapper.SocialNoticeMapper;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import com.leemon.wushiwan.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 系统通知
 * @author: leemon
 * @date: 2019-06-26
 */
@RestController
@RequestMapping("/social-notice")
@Slf4j
public class SocialNoticeController extends BaseController<SocialNotice> {

	private final ISocialNoticeService socialNoticeService;
	private final SocialNoticeMapper noticeMapper;

	@Autowired
	public SocialNoticeController(ISocialNoticeService socialNoticeService, SocialNoticeMapper noticeMapper) {
		this.socialNoticeService = socialNoticeService;
		this.noticeMapper = noticeMapper;
	}

	@RequestMapping(value = "/clist")
	public Object clist(@RequestBody @Valid BaseEntity entity) {
		Page<NoticeVO> page = new Page<>(entity.getPageNo(), entity.getPageSize());
		page.setDesc("create_time");
		return noticeMapper.selectNoticeList(page, UserUtil.getSysUserId());
	}

	/*----------------------------以下是admin的接口----------------------------**/

	/**
	 * 分页列表查询
	 *
	 * @param socialNotice
	 * @return
	 */
	@PreAuthorize("hasPermission('social_notice')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid SocialNotice socialNotice) {
		Page<SocialNotice> page = new Page<>(socialNotice.getPageNo(), socialNotice.getPageSize());
		return socialNoticeService.page(page, getLikeQueryWrapper(socialNotice));
	}

	/**
	 * 保存
	 *
	 * @param socialNotice
	 * @return
	 */
	@PreAuthorize("hasPermission('social_notice_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid SocialNotice socialNotice) {
		if (socialNotice.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		socialNoticeService.saveOrUpdate(socialNotice);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param socialNotice
	 * @return
	 */
	@PreAuthorize("hasPermission('social_notice_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid SocialNotice socialNotice) {
		if (socialNotice.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = socialNoticeService.updateById(socialNotice);
		if (!ok) {
			log.error("socialNotice = {}", socialNotice);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SocialNotice失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_notice_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		SocialNotice socialNotice = getExistEntityById(idRequest.getId(), socialNoticeService);
		boolean ok = socialNoticeService.removeById(socialNotice);
		if (!ok) {
			log.error("socialNotice = {}", socialNotice);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SocialNotice失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_notice_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		socialNoticeService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_notice')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return socialNoticeService.getById(idRequest.getId());
	}

}
