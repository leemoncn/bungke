package com.leemon.wushiwan.service.impl;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMission;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.entity.CoreTop;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.NoticeType;
import com.leemon.wushiwan.enums.SystemConfig;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreTopMapper;
import com.leemon.wushiwan.service.ICoreMissionService;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.service.ICoreTopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.RMBUtil;
import com.leemon.wushiwan.util.TimeUtil;
import com.leemon.wushiwan.util.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-07-25
 */
@Service
public class CoreTopServiceImpl extends ServiceImpl<CoreTopMapper, CoreTop> implements ICoreTopService {

	private final CoreTopMapper topMapper;
	private final ICorePartnerService partnerService;
	private final ISysUserService userService;
	private final ICoreMissionService missionService;
	private final ISocialNoticeService noticeService;

	public CoreTopServiceImpl(CoreTopMapper topMapper, ICorePartnerService partnerService, ISysUserService userService, ICoreMissionService missionService, ISocialNoticeService noticeService) {
		this.topMapper = topMapper;
		this.partnerService = partnerService;
		this.userService = userService;
		this.missionService = missionService;
		this.noticeService = noticeService;
	}

	/**
	 * 添加任务置顶记录
	 *
	 * @param missionId
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addTopMission(Integer missionId, Integer hour, MissionDetail missionDetail) {
		SysUser user = UserUtil.getSysUser();
		//今天剩余的免费时间
		int freeHoursToday = getFreeHourToday();
		//这一次能使用的小时数
		int freeHour = hour >= freeHoursToday ? freeHoursToday : freeHoursToday - hour;
		//这一次使用的付费小时数
		int paidHour = hour - freeHour;

		int price = paidHour * Integer.parseInt(SystemConfig.TOP_MISSION_PRICE.getPropertyValue());
		if (user.getMissionCoin() < price) {
			throw new LogicException("当前任务币%.02f元，置顶需要任务币%.02f币，任务币不足，请先充值", RMBUtil.fenToYuan(user.getMissionCoin()), RMBUtil.fenToYuan(price));
		}

		userService.updateUserMissionCoin(user, CurrencyChangeReasonType.TOP_MISSION, -price);

		//查出最近的一条置顶记录，看置顶时间是否过期了
		CoreTop lastCoreTop = topMapper.selectLastCoreTop(user.getId(), missionId);

		CoreTop ct = new CoreTop();
		ct.setMissionId(missionId);
		ct.setUserId(user.getId());
		ct.setFreeHours(freeHour);
		ct.setPaidHours(paidHour);
		ct.setPrice(price);
		LocalDateTime ldt = null;

		LocalDateTime now = LocalDateTime.now();
		if (lastCoreTop == null) {//之前这个任务没有置顶记录
			ldt = now;
		} else {//之前置顶过
			//之前的置顶截止时间
			LocalDateTime lastEndTime = lastCoreTop.getTopEndTime();
			if (lastEndTime.isAfter(now)) {//置顶未过期
				ldt = lastEndTime;
			} else {//置顶已过期
				ldt = now;
			}
		}
		ldt = ldt.plusHours(hour);
		ct.setTopEndTime(ldt);
		save(ct);

		CoreMission cm = new CoreMission();
		cm.setId(missionId);
		cm.setTopEndTime(ldt);
		missionService.updateById(cm);

		String msg = String.format("任务<%s>已置顶，置顶到期时间 %s", missionDetail.getTitle(), TimeUtil.formatTime(ldt));
		noticeService.sendNotice(NoticeType.TOP_MISSION, msg, user.getId());
	}

	@Override
	public int getFreeHourToday() {
		SysUser user = UserUtil.getSysUser();
		CorePartner partner = partnerService.getById(user.getPartnerId());
		//每天的免费推荐时间
		int freeHoursPerDay = partner.getAdHour();
		List<CoreTop> list = topMapper.selectTodayTopRecord(user.getId());
		//今天已经使用的免费时间
		int todayFreeHour = list.stream().mapToInt(CoreTop::getFreeHours).sum();
		//今天剩余的免费时间
		return todayFreeHour >= freeHoursPerDay ? 0 : freeHoursPerDay - todayFreeHour;
	}
}
