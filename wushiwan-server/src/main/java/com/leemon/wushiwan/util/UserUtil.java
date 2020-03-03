package com.leemon.wushiwan.util;

import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.FinancialWithdrawalMapper;
import com.leemon.wushiwan.system.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @author: limeng
 * @create: 2019-04-03 21:30
 **/
public class UserUtil {

	@Resource
	private FinancialWithdrawalMapper withdrawalMapper;

	private static UserUtil userUtil;

	@PostConstruct
	public void init() {
		userUtil = this;
		userUtil.withdrawalMapper = this.withdrawalMapper;
	}

	public static SysUser getSysUser() {
		return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public static Integer getSysUserId() {
		return getSysUser().getId();
	}

	public static List<String> getRoleNames() {
		Collection<? extends GrantedAuthority> authList = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		List<String> roleList = new ArrayList<>();
		for (GrantedAuthority grantedAuthority : authList) {
			roleList.add(grantedAuthority.getAuthority());
		}
		return roleList;
	}

	public static Integer getMoneyInReview(Integer userId) {
		if (userId == null) {
			throw new LogicException("userId为null");
		}
		return userUtil.withdrawalMapper.selectUserInReviewMoney(userId);
	}
}
