package com.leemon.wushiwan.dto;

import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.system.entity.SysUser;
import lombok.Data;

/**
 * @description: 登录返回结果
 * @author: leemon
 * @create: 2019-03-27 11:41
 **/
@Data
public class LoginResult {
	private String token;
	private SysUser sysUser;
	private CorePartner partner;
	private CoreAgency agency;
}
