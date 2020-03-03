package com.leemon.wushiwan.jwt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.leemon.wushiwan.entity.CoreLoginAuth;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.service.MessageService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.enums.LoginRegisterType;
import com.leemon.wushiwan.service.ICoreLoginAuthService;
import com.leemon.wushiwan.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * @description: 登录流程：
 * 1、未登录的情况下（前端没有存储的token），前端请求登录接口(登录接口配置的是无需验证，所以不会验证jwt的filter)，生成一个LoginAuthenticationToken类，类中只包含账号密码
 * 2、将LoginAuthenticationToken传入本类的authenticate方法进行验证
 * 3、账号密码验证成功后，生成一个包含user、auth、权限的完成的LoginAuthenticationToken，保存在内存中（本身这里是不需要保存的，但是害怕在登录接口中直接调用userutil中的一些方法，所以还是存进去了），然后将user、token等信息返回给前端
 * 4、前端携带token请求其他接口，进入jwtfilter进行验证token是否过期，如果没过期，将token中保存的userid取出，生成sysuser、权限等完整的JwtAuthenticationToken，放入内存中
 * 5、如果在filter中发现没有JwtAuthenticationToken，则最后在拦截器中抛出异常，提示前端登录
 * @author: limeng
 * @create: 2019-03-24 13:04
 **/
@Slf4j
public class LoginAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private ICoreLoginAuthService loginAuthService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private GALogic gaLogic;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private MessageService messageService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Authentication au = SecurityContextHolder.getContext().getAuthentication();
		if (au != null) {
			return au;
		}
		LoginAuthenticationToken loginToken = (LoginAuthenticationToken) authentication;
		CoreLoginAuth loginAuth = (CoreLoginAuth) loginToken.getCredentials();
		//密码加密
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		final String rawPassword = loginAuth.getPassword();
		QueryWrapper<CoreLoginAuth> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(CoreLoginAuth::getAccount, loginAuth.getAccount())
				.eq(CoreLoginAuth::getTypePropertyId, loginAuth.getTypePropertyId());

		CoreLoginAuth authResult = loginAuthService.getOne(queryWrapper);

		if (loginAuth.getTypePropertyId() == LoginRegisterType.WECHAT) {
			if (authResult != null) {
				return getAuthToken(authResult);
			} else {
				throw new LogicException("登陆失败，账号不存在");
			}
		} else if (loginAuth.getTypePropertyId() == LoginRegisterType.USERNAME_PASSWORD) {//后台管理员账号密码登陆
			if (authResult != null) {
				if (encoder.matches(rawPassword, authResult.getPassword())) {
					return getAuthToken(authResult);
				} else {// 密码错误
					throw new LogicException("登陆失败，账号不存在或密码错误");
				}
			} else {
				throw new LogicException("登陆失败，账号不存在或密码错误");
			}
		} else if (loginAuth.getTypePropertyId() == LoginRegisterType.MOBILE) {
			if (authResult != null) {
				String code = messageService.getCode(loginAuth.getAccount());
				if (Strings.isNullOrEmpty(code)) {//没有获取到redis里面的验证码
					throw new LogicException("登陆失败,请重新获取验证码");
				}
				if (code.equals(loginAuth.getPassword())) {//登陆成功
					messageService.destroyCacheCode(loginAuth.getAccount());
					return getAuthToken(authResult);
				} else {// 密码错误
					throw new LogicException("验证码错误");
				}
			} else {
				throw new LogicException("登陆失败，账号不存在或密码错误");
			}
		} else {
			throw new LogicException("需要添加相应的实现方式");
		}
	}

	private LoginAuthenticationToken getAuthToken(CoreLoginAuth loginAuth) {
		SysUser sysUser = sysUserService.getById(loginAuth.getUserId());
		List<RolePermissionGrantedAuthority> list = gaLogic.createGAList(sysUser);
		return new LoginAuthenticationToken(sysUser, loginAuth, list);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(LoginAuthenticationToken.class);
	}
}
