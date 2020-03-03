package com.leemon.wushiwan.entity.factory;

import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.enums.UserType;
import com.leemon.wushiwan.service.ICoreAgencyService;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.enums.LoginStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-27 11:28
 **/
@Component
public class SysUserFactory {


	private final ICorePartnerService corePartnerService;
	private final ICoreAgencyService agencyService;

	@Autowired
	public SysUserFactory(ICorePartnerService corePartnerService, ICoreAgencyService agencyService) {
		this.corePartnerService = corePartnerService;
		this.agencyService = agencyService;
	}

	public SysUser createSysUser(String phone, String nickname, String headImgUrl) {
		CorePartner cp = corePartnerService.getNonePartner();
		CoreAgency ca = agencyService.getExistCoreAgency();
		SysUser sysUser = new SysUser();
		sysUser.setEarning(0)
				.setAgencyId(ca.getId())
				.setPhone(phone)
				.setNickname(nickname)
				.setHeadImgUrl(headImgUrl)
				.setMissionCoin(0)
				.setDeposit(0)
				.setUserType(UserType.CLIENT)
				.setPartnerId(cp.getId())
				.setLoginStatusPropertyId(LoginStatus.ALLOW);
		return sysUser;
	}
}
