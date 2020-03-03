package com.leemon.wushiwan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.FinancialWithdrawalMapper;
import com.leemon.wushiwan.service.*;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.mapper.SysUserMapper;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.SpringUtil;
import com.leemon.wushiwan.vo.SysUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

	private final ICoreCurrencyChangeService coreCurrencyChangeService;
	private final ICoreAgencyService agencyService;
	private final ICorePartnerService partnerService;
	@Autowired
	private IFinancialWithdrawalService withdrawalService;
	private final FinancialWithdrawalMapper withdrawalMapper;
	@Autowired
	private ISocialNoticeService noticeService;
	private final SysUserMapper sysUserMapper;

	@Autowired
	public SysUserServiceImpl(ICoreCurrencyChangeService coreCurrencyChangeService, ICoreAgencyService agencyService, ICorePartnerService partnerService, FinancialWithdrawalMapper withdrawalMapper, SysUserMapper sysUserMapper) {
		this.coreCurrencyChangeService = coreCurrencyChangeService;
		this.agencyService = agencyService;
		this.partnerService = partnerService;
		this.withdrawalMapper = withdrawalMapper;
		this.sysUserMapper = sysUserMapper;
	}

	@Cacheable(value = "SysUser", key = "#id")
	@Override
	public SysUser getById(Serializable id) {
		SysUser user = super.getById(id);
		if (user == null) {
			return null;
		}
		return user;
	}

	@Override
	@Cacheable(value = "SysUserDetail", key = "#id")
	public SysUserDetail getSysUserDetailById(Serializable id) {
		SysUserDetail detail = sysUserMapper.selectSysUserDetailById((Integer) id);
		if (detail == null) {
			return null;
		}
		return detail;
	}

	@Override
	public void removePushIdFromAllUser(String pushId) {
		QueryWrapper<SysUser> qw = new QueryWrapper<>();
		qw.lambda().eq(SysUser::getPushId, pushId);
		//理论上list只有一个值
		List<SysUser> list = this.list(qw);
		for (SysUser sysUser : list) {
			SysUser updateUser = new SysUser();
			updateUser.setId(sysUser.getId());
			updateUser.setPushId(null);
			this.updateById(updateUser);
		}
	}

	@Caching(evict = {
			@CacheEvict(value = "SysUser", key = "#entity.id"),
			@CacheEvict(value = "SysUserDetail", key = "#entity.id")
	})
	@Override
	public boolean updateById(SysUser entity) {
		return super.updateById(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysUser", key = "#entity.id", condition = "#entity.id != null"),
			@CacheEvict(value = "SysUserDetail", key = "#entity.id", condition = "#entity.id != null")
	})
	@Override
	public boolean saveOrUpdate(SysUser entity) {
		return super.saveOrUpdate(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysUser", allEntries = true),
			@CacheEvict(value = "SysUserDetail", allEntries = true)
	})
	@Override
	public boolean update(SysUser entity, Wrapper<SysUser> updateWrapper) {
		return super.update(entity, updateWrapper);
	}

	//TODO 肯定是有并发问题的
	@Caching(evict = {
			@CacheEvict(value = "SysUser", key = "#user.id"),
			@CacheEvict(value = "SysUserDetail", key = "#user.id")
	})
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateUserMissionCoin(SysUser user, CurrencyChangeReasonType type, int change) {
		if (change == 0) {
			return;
		}
		//自己的变化后的任务币数值
		int newMissionCoin = user.getMissionCoin();
		if (type == CurrencyChangeReasonType.RECHARGE_MISSION_COIN) {//充值任务币，系统给上级返现
			if (change < 0) {
				throw new LogicException(ErrorCode.SYS_ERROR, "充值任务币，数值变化不能是负值");
			}
			newMissionCoin += change;
			Integer superiorId = user.getSuperiorId();
			if (superiorId != null) {
				Integer agencyId = user.getAgencyId();
				SysUser superiorUser = getById(superiorId);
				CoreAgency ca = agencyService.getById(agencyId);
				//系统出钱，给上级的返现金额,单位分（负值）
				int toSuperiorEarning = BigDecimal.valueOf(ca.getRechargeByApp()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(change)).intValue();
				if (toSuperiorEarning != 0) {
					getCurrentSpringWiredService().updateUserEarning(superiorUser, CurrencyChangeReasonType.EARNING_BY_SUBORDINATE_RECHARGE_MISSION_COIN, toSuperiorEarning);
				}
			}
		} else if (type == CurrencyChangeReasonType.WITHDRAWAL_MISSION_COIN) {//提现任务币，扣手续费
			if (change > 0) {
				throw new LogicException(ErrorCode.SYS_ERROR, "提现任务币，数值变化不能是正值");
			}
			Integer partnerId = user.getPartnerId();
			CorePartner cp = partnerService.getById(partnerId);
			//扣除的手续费金额
			int fee = BigDecimal.valueOf(cp.getMissionPaymentPercent()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(change)).intValue();
			newMissionCoin += (change + fee);
			if (newMissionCoin < 0) {
				throw new LogicException("当前任务币不足");
			}
			//发出提现申请
			withdrawalService.requestWithdrawal(user.getId(), Math.abs(change), CurrencyChangeReasonType.WITHDRAWAL_MISSION_COIN);
			//添加手续费流水记录
			coreCurrencyChangeService.addCurrencyChangeRecord(user.getId(), CurrencyType.MISSION_COIN, CurrencyChangeReasonType.WITHDRAWAL_MISSION_COIN_FEE, fee);
		} else {//其他情况，比如发布任务扣任务币等
			newMissionCoin += change;
			if (newMissionCoin < 0) {
				throw new LogicException("当前任务币不足");
			}
		}
		SysUser query = new SysUser();
		query.setId(user.getId());
		query.setMissionCoin(newMissionCoin);
		if (updateById(query)) {
			user.setMissionCoin(newMissionCoin);
		} else {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新User的任务币出错");
		}
		coreCurrencyChangeService.addCurrencyChangeRecord(user.getId(), CurrencyType.MISSION_COIN, type, change);
	}

	/**
	 * 更新用户收入
	 *
	 * @param user   更新谁
	 * @param change 数量，可以正数或者负数
	 */
	@Caching(evict = {
			@CacheEvict(value = "SysUser", key = "#user.id"),
			@CacheEvict(value = "SysUserDetail", key = "#user.id")
	})
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateUserEarning(SysUser user, CurrencyChangeReasonType type, int change) {
		if (change == 0) {
			return;
		}
		int newEarning = user.getEarning();
		if (type == CurrencyChangeReasonType.WITHDRAWAL_EARNING) {//提现收入
			if (change > 0) {
				throw new LogicException(ErrorCode.SYS_ERROR, "提现收入，数值变化不能是正值");
			}
			Integer agencyId = user.getAgencyId();
			CoreAgency ca = agencyService.getById(agencyId);
			//手续费分成(负值)
			int fee = BigDecimal.valueOf(ca.getWithdrawByUser()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(change)).intValue();
			newEarning += (change + fee);
			if (newEarning < 0) {
				throw new LogicException("当前收入不足");
			}
			//发出提现申请,审核通过后，再给上级返现收入
			withdrawalService.requestWithdrawal(user.getId(), Math.abs(change), CurrencyChangeReasonType.WITHDRAWAL_EARNING);
			//添加提现手续费流水记录
			coreCurrencyChangeService.addCurrencyChangeRecord(user.getId(), CurrencyType.EARNING, CurrencyChangeReasonType.WITHDRAWAL_EARNING_FEE, fee);
		} else {
			newEarning += change;
			if (newEarning < 0) {
				throw new LogicException("当前收入不足");
			}
		}
		SysUser query = new SysUser();
		query.setId(user.getId());
		query.setEarning(newEarning);
		if (updateById(query)) {
			user.setEarning(newEarning);
		} else {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新User的收入出错");
		}
		coreCurrencyChangeService.addCurrencyChangeRecord(user.getId(), CurrencyType.EARNING, type, change);
	}

	/**
	 * 更新用户的合作商ID
	 *
	 * @param user
	 * @param partnerId
	 */
	@Caching(evict = {
			@CacheEvict(value = "SysUser", key = "#user.id"),
			@CacheEvict(value = "SysUserDetail", key = "#user.id")
	})
	@Override
	public void updateUserPartnerId(SysUser user, int partnerId) {
		SysUser query = new SysUser();
		query.setId(user.getId());
		query.setPartnerId(partnerId);
		if (updateById(query)) {
			user.setPartnerId(partnerId);
		} else {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新User的合作商ID出错");
		}
	}

	/**
	 * 更新用户保证金
	 *
	 * @param user
	 * @param deposit
	 */
	@Caching(evict = {
			@CacheEvict(value = "SysUser", key = "#user.id"),
			@CacheEvict(value = "SysUserDetail", key = "#user.id")
	})
	@Override
	public void updateUserDeposit(SysUser user, int deposit) {
		SysUser query = new SysUser();
		query.setId(user.getId());
		query.setDeposit(deposit);
		if (updateById(query)) {
			user.setDeposit(deposit);
		} else {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新User的保证金出错");
		}
	}

	/**
	 * 更新用户支付宝账号和真实姓名
	 *
	 * @param user
	 * @param account
	 * @param realName
	 */
	@Caching(evict = {
			@CacheEvict(value = "SysUser", key = "#user.id"),
			@CacheEvict(value = "SysUserDetail", key = "#user.id")
	})
	@Override
	public void updateUserAlipayAccountAndRealName(SysUser user, String account, String realName) {
		SysUser query = new SysUser();
		query.setId(user.getId());
		query.setAlipay(account);
		query.setRealName(realName);
		if (updateById(query)) {
			user.setAlipay(account);
			user.setRealName(realName);
		} else {
			throw new LogicException(ErrorCode.SYS_ERROR, "更新User的支付宝和真实姓名出错");
		}
	}

	private ISysUserService getCurrentSpringWiredService() {
		return SpringUtil.getBean(ISysUserService.class);
	}


}
