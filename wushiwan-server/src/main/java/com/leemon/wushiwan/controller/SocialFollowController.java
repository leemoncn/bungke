package com.leemon.wushiwan.controller;

import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.dto.FansDetail;
import com.leemon.wushiwan.entity.BaseEntity;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.SocialFollow;
import com.leemon.wushiwan.mapper.SocialFollowMapper;
import com.leemon.wushiwan.service.ISocialFollowService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
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
 * @Description: 关注记录
 * @author: leemon
 * @date: 2019-07-15
 */
@RestController
@RequestMapping("/social-follow")
@Slf4j
public class SocialFollowController extends BaseController<SocialFollow> {

	private final ISocialFollowService socialFollowService;
	private final SocialFollowMapper followMapper;
	private final ISysUserService sysUserService;

	@Autowired
	public SocialFollowController(ISocialFollowService socialFollowService, SocialFollowMapper followMapper, ISysUserService sysUserService) {
		this.socialFollowService = socialFollowService;
		this.followMapper = followMapper;
		this.sysUserService = sysUserService;
	}

	/**
	 * 我的关注列表
	 *
	 * @param entity
	 * @return
	 */
	@RequestMapping("/follow-list")
	public Object followList(@Valid @RequestBody BaseEntity entity) {
		Page<SysUser> page = new Page<>(entity.getPageNo(), entity.getPageSize());
		followMapper.selectFollowedUserList(page, UserUtil.getSysUserId());
		return page;
	}

	/**
	 * 我的粉丝列表
	 *
	 * @param entity
	 * @return
	 */
	@RequestMapping("/fans-list")
	public Object fansList(@Valid @RequestBody BaseEntity entity) {
		Page<SysUser> page = new Page<>(entity.getPageNo(), entity.getPageSize());
		followMapper.selectFansUserList(page, UserUtil.getSysUserId());
		return page;
	}

	@Data
	private static class FollowResponse {
		private List<FansDetail> fansList;
		private List<FansDetail> followList;
	}

	/**
	 * 我的粉丝及我的关注列表
	 *
	 * @return
	 */
	@PostMapping("/follow/list")
	public FollowResponse followList() {
		FollowResponse res = new FollowResponse();

		Page<FansDetail> fansPage = new Page<>(1, 99999);
		List<FansDetail> fansList = followMapper.selectFansUserList(fansPage, UserUtil.getSysUserId()).getRecords();

		Page<FansDetail> followPage = new Page<>(1, 99999);
		List<FansDetail> followList = followMapper.selectFollowedUserList(followPage, UserUtil.getSysUserId()).getRecords();

		res.setFansList(fansList);
		res.setFollowList(followList);
		return res;
	}

	@Data
	private static class FollowRequest {
		@NotNull
		private Integer followId;
		@NotNull
		private Boolean followResult;//关注还是取关
	}

	/**
	 * 关注或者取关某一个用户
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/follow")
	public Object fansList(@Valid @RequestBody FollowRequest req) {
		QueryWrapper<SocialFollow> qw = new QueryWrapper<>();
		qw.lambda().eq(SocialFollow::getFromUserId, UserUtil.getSysUserId())
				.eq(SocialFollow::getToUserId, req.getFollowId());
		SocialFollow sf = socialFollowService.getOne(qw);
		if (req.getFollowResult()) {//关注
			if (sf != null) {//已关注，无需重复关注
				return null;
			}
			if (req.getFollowId().equals(UserUtil.getSysUserId())) {
				throw new LogicException("无法关注自己");
			}
			sf = new SocialFollow();
			sf.setFromUserId(UserUtil.getSysUserId());
			sf.setToUserId(req.getFollowId());
			socialFollowService.save(sf);
		} else {//取关
			if (sf == null) {//没有关注，无需取关
				return null;
			}
			socialFollowService.removeById(sf.getId());
		}
		return null;
	}

	/*----------------------------以下是admin的接口----------------------------**/

	/**
	 * 分页列表查询
	 *
	 * @param socialFollow
	 * @return
	 */
	@PreAuthorize("hasPermission('social_follow')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid SocialFollow socialFollow) {
		Page<SocialFollow> page = new Page<>(socialFollow.getPageNo(), socialFollow.getPageSize());
		return socialFollowService.page(page, getLikeQueryWrapper(socialFollow));
	}

	/**
	 * 保存
	 *
	 * @param socialFollow
	 * @return
	 */
	@PreAuthorize("hasPermission('social_follow_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid SocialFollow socialFollow) {
		if (socialFollow.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		socialFollowService.saveOrUpdate(socialFollow);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param socialFollow
	 * @return
	 */
	@PreAuthorize("hasPermission('social_follow_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid SocialFollow socialFollow) {
		if (socialFollow.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = socialFollowService.updateById(socialFollow);
		if (!ok) {
			log.error("socialFollow = {}", socialFollow);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SocialFollow失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_follow_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		SocialFollow socialFollow = getExistEntityById(idRequest.getId(), socialFollowService);
		boolean ok = socialFollowService.removeById(socialFollow);
		if (!ok) {
			log.error("socialFollow = {}", socialFollow);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SocialFollow失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_follow_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		socialFollowService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_follow')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return socialFollowService.getById(idRequest.getId());
	}

}
