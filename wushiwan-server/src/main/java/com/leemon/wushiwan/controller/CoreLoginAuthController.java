package com.leemon.wushiwan.controller;


import com.baomidou.mybatisplus.annotation.TableField;
import com.google.common.base.Strings;
import com.leemon.wushiwan.config.JwtProperties;
import com.leemon.wushiwan.dto.PartnerInfo;
import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.entity.CoreLoginAuth;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.dto.LoginResult;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.jwt.JwtTokenUtil;
import com.leemon.wushiwan.mapper.CorePartnerPurchaseMapper;
import com.leemon.wushiwan.service.ICoreAgencyService;
import com.leemon.wushiwan.service.ICoreLoginAuthService;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.RegisterRequest;
import com.leemon.wushiwan.vo.StringRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
@RequestMapping("/core-login-auth")

public class CoreLoginAuthController {

	private final ICoreLoginAuthService coreLoginAuthService;
	private final JwtProperties jwtProperties;
	private final CacheManager cacheManager;
	private final ICorePartnerService partnerService;
	private final ICoreAgencyService agencyService;
	private final ISysUserService userService;
	private final CorePartnerPurchaseMapper corePartnerPurchaseMapper;

	@Autowired
	public CoreLoginAuthController(ICoreLoginAuthService coreLoginAuthService, JwtProperties jwtProperties, JwtTokenUtil jwtTokenUtil, CacheManager cacheManager, ICorePartnerService partnerService, ICoreAgencyService agencyService, ISysUserService userService, CorePartnerPurchaseMapper corePartnerPurchaseMapper) {
		this.jwtProperties = jwtProperties;
		this.coreLoginAuthService = coreLoginAuthService;
		this.cacheManager = cacheManager;
		this.partnerService = partnerService;
		this.agencyService = agencyService;
		this.userService = userService;
		this.corePartnerPurchaseMapper = corePartnerPurchaseMapper;
	}

	@RequestMapping("/no-auth/login")
	public Object login(@RequestBody @Valid CoreLoginAuth coreLoginAuth, HttpServletRequest request) {
		return coreLoginAuthService.login(coreLoginAuth);
	}

	@RequestMapping("/no-auth/register")
	public Object register(@RequestBody @Valid RegisterRequest req) {
		return coreLoginAuthService.register(req.getMobileRegisterData(), req.getOtherRegisterData(), req.getUuid());
	}

	@Data
	private static class RefreshRequest {
		private String pushId;
		private String deviceInfo;
	}

	@RequestMapping("/refresh")
	public Object refresh(@RequestBody @Valid RefreshRequest req) {
		LoginResult result = new LoginResult();
		SysUser loginUser = UserUtil.getSysUser();
		SysUser updateUser = new SysUser();
		updateUser.setId(loginUser.getId());

		String newPushId = req.getPushId();
		String deviceInfo = req.getDeviceInfo();
		if (!Strings.isNullOrEmpty(newPushId)) {
			if (Strings.isNullOrEmpty(loginUser.getPushId()) || !loginUser.getPushId().equals(newPushId)) {
				loginUser.setPushId(newPushId);
				updateUser.setPushId(newPushId);
			}
		}
		if (!Strings.isNullOrEmpty(deviceInfo)) {
			if (Strings.isNullOrEmpty(loginUser.getDeviceInfo()) || !loginUser.getDeviceInfo().equals(deviceInfo)) {
				loginUser.setDeviceInfo(deviceInfo);
				updateUser.setDeviceInfo(deviceInfo);
			}
		}
		if (!Strings.isNullOrEmpty(updateUser.getPushId()) || !Strings.isNullOrEmpty(updateUser.getDeviceInfo())) {
			if (!Strings.isNullOrEmpty(updateUser.getPushId())) {//把这个pushid从其他用户移除
				userService.removePushIdFromAllUser(updateUser.getPushId());
			}
			userService.updateById(updateUser);
		}

		result.setSysUser(loginUser);
		CorePartner userCurrentPartner = corePartnerPurchaseMapper.selectValidPartnerInfoByUser(loginUser.getId());
		if (userCurrentPartner == null) {
			userCurrentPartner = partnerService.getNonePartner();
		}
		result.setPartner(userCurrentPartner);
		CoreAgency ca = agencyService.getById(loginUser.getAgencyId());
		result.setAgency(ca);
		return result;
	}

	@RequestMapping("/logout")
	public Object logout(HttpServletRequest request) {
		String authHeader = request.getHeader(jwtProperties.getHeader());
		coreLoginAuthService.logout(authHeader);
		return null;
	}


	@RequestMapping("/no-auth/vcode")
	public Object vcode(@RequestBody @Valid StringRequest request) {
		String phone = request.getStr();
		if (phone.length() != 11) {
			throw new LogicException("手机号不合法");
		}
		coreLoginAuthService.sendVCode(request.getStr());
		return null;
	}

}
