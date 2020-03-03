package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.dto.PartnerInfo;
import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.entity.CoreLoginAuth;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.enums.LoginRegisterType;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CorePartnerPurchaseMapper;
import com.leemon.wushiwan.service.*;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.dto.LoginResult;
import com.leemon.wushiwan.entity.factory.SysUserFactory;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.jwt.JwtTokenUtil;
import com.leemon.wushiwan.jwt.LoginAuthenticationToken;
import com.leemon.wushiwan.mapper.CoreLoginAuthMapper;
import com.leemon.wushiwan.system.service.ISysRoleService;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.LoginUtil;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.util.WebUtil;
import com.leemon.wushiwan.vo.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Slf4j
@Service
public class CoreLoginAuthServiceImpl extends ServiceImpl<CoreLoginAuthMapper, CoreLoginAuth> implements ICoreLoginAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenUtil jwtTokenUtil;
	private final ISysUserService sysUserService;
	private final WebUtil webUtil;
	private final CacheManager cacheManager;
	private final ISysRoleService sysRoleService;
	private final MessageService messageService;
	private final SysUserFactory sysUserFactory;
	private final ICoreQrcodeService qrcodeService;
	private final ICorePartnerService partnerService;
	private final ICoreAgencyService agencyService;
	private final Config config;
	private final CorePartnerPurchaseMapper corePartnerPurchaseMapper;

	@Autowired
	public CoreLoginAuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, ISysUserService sysUserService, WebUtil webUtil, CacheManager cacheManager, ISysRoleService sysRoleService, MessageService messageService, SysUserFactory sysUserFactory, ICoreQrcodeService qrcodeService, ICorePartnerService partnerService, ICoreAgencyService agencyService, Config config, CorePartnerPurchaseMapper corePartnerPurchaseMapper) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
		this.sysUserService = sysUserService;
		this.webUtil = webUtil;
		this.cacheManager = cacheManager;
		this.sysRoleService = sysRoleService;
		this.messageService = messageService;
		this.sysUserFactory = sysUserFactory;
		this.qrcodeService = qrcodeService;
		this.partnerService = partnerService;
		this.agencyService = agencyService;
		this.config = config;
		this.corePartnerPurchaseMapper = corePartnerPurchaseMapper;
	}

	/**
	 * 只有手机验证码的注册方式或者其他注册方式和手机号注册一起发来，才应该添加新用户
	 * 如果是微信登录方式发过来，先查询是否有注册记录，如果有，说明微信和手机肯定都已经注册了，这时候返回user对象，前端直接调用登录。如果没有返回null，前端要跳到手机号页面绑定
	 * 一共4种流程
	 * 1、从没注册过，网页版直接手机号注册(mobile)、登录
	 * 2、从没注册过，APP版直接微信注册(other)，后台查询是否有微信loginauth，没有，返回null（不注册新用户，不添加loginauth），前端跳手机号页面，把手机号和微信data一起发给后台(other mobile)，后台创建新用户，前端再登录
	 * 3、网页版手机号注册过，再登录APP版，APP版直接微信注册(other)，后台查询是否有微信loginauth，没有，后台返回null（不注册新用户，不添加loginauth），前端跳手机号页面，把手机号和微信data一起发给后台(other mobile)，后台查找已注册的手机号记录，找到userid，把微信的消息绑定到user上，再添加微信的loginauth
	 * 4、以前完成过APP的注册流程，然后微信注册(other)，后台查到记录后直接返回user，前端再直接调用登录
	 *
	 * @param mobileRequest
	 * @param otherRequest
	 * @param parentUuid
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean register(RegisterRequest.RegisterData mobileRequest, RegisterRequest.RegisterData otherRequest, String parentUuid) {
		CoreLoginAuth otherLoginAuth = null;
		if (otherRequest != null && mobileRequest == null) {
			QueryWrapper<CoreLoginAuth> qw = new QueryWrapper<>();
			qw.lambda().eq(CoreLoginAuth::getAccount, otherRequest.getAccount())
					.eq(CoreLoginAuth::getTypePropertyId, otherRequest.getTypePropertyId());
			otherLoginAuth = getOne(qw);
			//没有绑定手机号
			return otherLoginAuth != null;
		}

		QueryWrapper<CoreLoginAuth> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreLoginAuth::getAccount, mobileRequest.getAccount())
				.eq(CoreLoginAuth::getTypePropertyId, mobileRequest.getTypePropertyId());
		CoreLoginAuth mobileLoginAuth = getOne(qw);

		if (otherRequest == null) {//网页mobile直接注册
			if (mobileLoginAuth != null) {//手机号之前注册过
				return true;
			}
		}
		//以下只有两种可能，mobile注册过和mobile没注册过，other肯定没注册过

		Integer superiorId = null;
		if (!Strings.isNullOrEmpty(parentUuid)) {//通过别人分享的二维码注册的，添加上级
			SysUser superiorUser = qrcodeService.getSysUserByUUID(parentUuid);
			superiorId = superiorUser.getId();
		}

		SysUser sysUser;
		if (mobileLoginAuth == null) {//手机号没有注册过
			String code = messageService.getCode(mobileRequest.getAccount());
			if (Strings.isNullOrEmpty(code) || !code.equals(mobileRequest.getPassword())) {
				throw new LogicException("手机验证码错误，请重新获取");
			}
			CoreLoginAuth mobileRegisterAuth = new CoreLoginAuth();
			mobileRegisterAuth.setAccount(mobileRequest.getAccount());
			mobileRegisterAuth.setTypePropertyId(mobileRequest.getTypePropertyId());
			mobileRegisterAuth.setData(mobileRequest.getData());
			String nickname = otherRequest == null ? null : otherRequest.getNicknameFromData();
			String headImgUrl = otherRequest == null ? null : otherRequest.getHeadImgUrlFromData();
			sysUser = registerNewUser(mobileRegisterAuth.getAccount(), nickname, headImgUrl, superiorId);
			mobileRegisterAuth.setUserId(sysUser.getId());
			saveLoginAuth(mobileRegisterAuth);
		} else {//手机号注册过,说明是之前在网页版注册过，然后登陆了APP版
			//更新昵称和头像
			sysUser = sysUserService.getById(mobileLoginAuth.getUserId());
			sysUser.setNickname(otherRequest.getNicknameFromData());
			sysUser.setHeadImgUrl(otherRequest.getHeadImgUrlFromData());
			sysUserService.updateById(sysUser);
		}
		if (otherRequest != null) {//三方登陆
			CoreLoginAuth otherRegisterAuth = new CoreLoginAuth();
			otherRegisterAuth.setAccount(otherRequest.getAccount());
			otherRegisterAuth.setTypePropertyId(otherRequest.getTypePropertyId());
			otherRegisterAuth.setData(otherRequest.getData());
			otherRegisterAuth.setUserId(sysUser.getId());
			saveLoginAuth(otherRegisterAuth);
			messageService.destroyCacheCode(mobileRequest.getAccount());
		}
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public LoginResult login(CoreLoginAuth loginAuth) {
		LoginAuthenticationToken upToken = new LoginAuthenticationToken(loginAuth);
		final Authentication authentication = authenticationManager.authenticate(upToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		SysUser loginUser = UserUtil.getSysUser();
		// 更新登录IP和时间
		loginUser.setLoginTime(LocalDateTime.now());
		loginUser.setLoginIp(webUtil.getClientIp());
		if (!Strings.isNullOrEmpty(loginAuth.getPushId())) {
			if (Strings.isNullOrEmpty(loginUser.getPushId()) || !loginUser.getPushId().equals(loginAuth.getPushId())) {
				sysUserService.removePushIdFromAllUser(loginAuth.getPushId());
				loginUser.setPushId(loginAuth.getPushId());
			}
		}
		loginUser.setDeviceInfo(loginAuth.getDeviceInfo());
		//如果是三方登陆，更新loginauth和用户的昵称、头像
		if (loginAuth.getTypePropertyId() == LoginRegisterType.WECHAT) {
			if (!Strings.isNullOrEmpty(loginAuth.getData())) {
				String nickname = LoginUtil.getNicknameFromDataForWechat(loginAuth.getData());
				String headImgUrl = LoginUtil.getHeadImgUrlFromDataForWechat(loginAuth.getData());
				loginUser.setNickname(nickname);
				loginUser.setHeadImgUrl(headImgUrl);

				QueryWrapper<CoreLoginAuth> qw = new QueryWrapper<>();
				qw.lambda().eq(CoreLoginAuth::getAccount, loginAuth.getAccount())
						.eq(CoreLoginAuth::getTypePropertyId, loginAuth.getTypePropertyId());
				CoreLoginAuth otherLoginAuth = getOne(qw);
				if (otherLoginAuth != null) {
					otherLoginAuth.setData(loginAuth.getData());
					updateById(otherLoginAuth);
				}
			}
		}
		sysUserService.updateById(loginUser);
		String token = jwtTokenUtil.generateToken(loginUser, String.valueOf(loginUser.getId()));
		LoginResult result = new LoginResult();
		result.setSysUser(loginUser);
		result.setToken(token);
		CorePartner userCurrentPartner = corePartnerPurchaseMapper.selectValidPartnerInfoByUser(loginUser.getId());
		if (userCurrentPartner == null) {
			userCurrentPartner = partnerService.getNonePartner();
		}
		result.setPartner(userCurrentPartner);
		CoreAgency ca = agencyService.getById(loginUser.getAgencyId());
		result.setAgency(ca);
		//将token设置到redis中的白名单中，只有白名单的token合法，其他设备已经登录过的token自动失效，保证同时只有一个登录的
		jwtTokenUtil.addTokenToWhiteList(token);
		return result;
	}

	@Override
	public void logout(String token) {
		//TODO 这里，登出应该调用的，清空pushId
//		jwtTokenUtil.addTokenToBlackList(token);
	}

	@Override
	public String refresh(String oldToken) {
		if (jwtTokenUtil.canTokenBeRefreshed(oldToken)) {
			String newToken = jwtTokenUtil.refreshToken(oldToken);
			jwtTokenUtil.addTokenToWhiteList(newToken);
			return newToken;
		}
		//token已过期
		throw new LogicException(ErrorCode.LOGIN_FAILED);
	}

	/**
	 * 前端用户注册新用户
	 *
	 * @param phone
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public SysUser registerNewUser(String phone, String nickname, String headImgUrl, Integer superiorId) {
		SysUser sysUser = sysUserFactory.createSysUser(phone, nickname, headImgUrl);
		if (superiorId == null) {//如果不是通过上级分享二维码注册的，则默认给一个系统用户为上级
			superiorId = config.getSystemSuperiorId();
		}
		sysUser.setSuperiorId(superiorId);
		if (!sysUserService.save(sysUser)) {
			throw new LogicException("注册新用户向数据库保存用户失败");
		}
		if (Strings.isNullOrEmpty(nickname)) {
			sysUser.setNickname("用户" + sysUser.getId());
			if (!sysUserService.updateById(sysUser)) {
				throw new LogicException("更新用户向数据库保存用户失败");
			}
		}
		return sysUser;
	}

	@Override
	public void sendVCode(String phone) {
		messageService.sendMessage(phone);
	}

	private void saveLoginAuth(CoreLoginAuth loginAuth) {
		if (!this.save(loginAuth)) {
			throw new LogicException("注册新用户向数据库保存AUTH失败");
		}
	}
}
