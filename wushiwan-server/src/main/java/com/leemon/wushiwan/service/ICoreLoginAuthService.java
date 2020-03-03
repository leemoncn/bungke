package com.leemon.wushiwan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.entity.CoreLoginAuth;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.dto.LoginResult;
import com.leemon.wushiwan.vo.RegisterRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ICoreLoginAuthService extends IService<CoreLoginAuth> {

	Boolean register(RegisterRequest.RegisterData mobileRequest, RegisterRequest.RegisterData otherRequest, String parentUuid);

	LoginResult login(CoreLoginAuth loginAuth);

	void logout(String token);

	String refresh(String oldToken);

	SysUser registerNewUser(String phone, String nickname, String headImgUrl, Integer superiorId);

	void sendVCode(String phone);
}
