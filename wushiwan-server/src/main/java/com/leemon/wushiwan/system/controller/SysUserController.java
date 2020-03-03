package com.leemon.wushiwan.system.controller;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.controller.BaseController;
import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.enums.LoginStatus;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.jwt.JwtTokenUtil;
import com.leemon.wushiwan.service.ICoreAgencyService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Slf4j
@RestController
@RequestMapping("/sys-user")
public class SysUserController extends BaseController<SysUser> {

	private final ISysUserService sysUserService;
	private final JwtTokenUtil jwtTokenUtil;
	private final ICoreAgencyService agencyService;

	@Autowired
	public SysUserController(ISysUserService sysUserService, JwtTokenUtil jwtTokenUtil, ICoreAgencyService agencyService) {
		this.sysUserService = sysUserService;
		this.jwtTokenUtil = jwtTokenUtil;
		this.agencyService = agencyService;
	}

	@Data
	private static class AlipayAccountRequest {
		@NotBlank
		private String account;
		@NotBlank
		private String realName;
	}


	/**
	 * 绑定支付宝账号
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/set-alipay")
	public Object queryDetailById(@RequestBody @Valid AlipayAccountRequest req) {
		SysUser user = UserUtil.getSysUser();
		sysUserService.updateUserAlipayAccountAndRealName(user, req.getAccount(), req.getRealName());
		return user;
	}

	@Data
	private static class Subordinate {
		private String name;
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime registerTime;
		private String headImgUrl;
		private Integer id;
	}

	@Data
	@Accessors(chain = true)
	private static class SubordinateResponse {
		private List<Subordinate> subordinateList;
		private CoreAgency coreAgency;
	}

	/**
	 * 我的下线列表
	 *
	 * @return
	 */
	@RequestMapping(value = "/subordinate")
	public SubordinateResponse subordinateList() {
		SysUser user = UserUtil.getSysUser();
		QueryWrapper<SysUser> qw = new QueryWrapper<>();
		qw.lambda().eq(SysUser::getSuperiorId, user.getId());
		List<SysUser> list = sysUserService.list(qw);
		List<Subordinate> l = new ArrayList<>();
		list.forEach(sysUser -> {
			Subordinate res = new Subordinate();
			res.setName(sysUser.getNickname());
			res.setHeadImgUrl(sysUser.getHeadImgUrl());
			res.setRegisterTime(sysUser.getCreateTime());
			res.setId(sysUser.getId());
			l.add(res);
		});
		CoreAgency ca = agencyService.getById(user.getAgencyId());

		SubordinateResponse res = new SubordinateResponse();
		res.setCoreAgency(ca).setSubordinateList(l);
		return res;
	}

	/*----------------------------以下是admin的接口----------------------------**/

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_user_view')")
	@RequestMapping(value = "/queryDetailById")
	public Object queryDetailById(@RequestBody @Valid IdRequest idRequest) {
		return sysUserService.getSysUserDetailById(idRequest.getId());
	}

	/**
	 * 分页列表查询
	 *
	 * @param sysUser
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_user_view')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid SysUser sysUser) {
		Page<SysUser> page = new Page<>(sysUser.getPageNo(), sysUser.getPageSize());
		return sysUserService.page(page, getLikeQueryWrapper(sysUser));
	}

	/**
	 * 保存
	 *
	 * @param sysUser
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_user_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid SysUser sysUser) {
		if (sysUser.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		sysUserService.saveOrUpdate(sysUser);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param sysUser
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@PreAuthorize("hasPermission('sys_user_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid SysUser sysUser) {
		if (sysUser.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = sysUserService.updateById(sysUser);
		if (!ok) {
			log.error("sysUser = {}", sysUser);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysUser失败");
		}
		if (sysUser.getLoginStatusPropertyId() != null && sysUser.getLoginStatusPropertyId() == LoginStatus.DENIED) {//禁止登录，需要移除已经保存的白名单
			jwtTokenUtil.removeWhiteList(sysUser.getId());
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_user_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		SysUser sysUser = getExistEntityById(idRequest.getId(), sysUserService);
		boolean ok = sysUserService.removeById(sysUser);
		if (!ok) {
			log.error("sysUser = {}", sysUser);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysUser失败");
		}
		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_user_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		sysUserService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('sys_user_view')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return sysUserService.getById(idRequest.getId());
	}
}
