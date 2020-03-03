package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMission;
import com.leemon.wushiwan.entity.CoreMissionAccept;
import com.leemon.wushiwan.entity.CoreMissionDetail;
import com.leemon.wushiwan.enums.*;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreMissionDetailMapper;
import com.leemon.wushiwan.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.RMBUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
@Slf4j
public class CoreMissionDetailServiceImpl extends ServiceImpl<CoreMissionDetailMapper, CoreMissionDetail> implements ICoreMissionDetailService {

	private final CoreMissionDetailMapper coreMissionDetailMapper;
	private final ISysUserService userService;
	private final ICorePartnerService corePartnerService;
	private final ICoreMissionAcceptService acceptService;
	@Autowired
	private ICoreMissionService missionService;
	private final ISocialNoticeService noticeService;

	public CoreMissionDetailServiceImpl(CoreMissionDetailMapper coreMissionDetailMapper, ISysUserService userService, ICorePartnerService corePartnerService, ICoreMissionAcceptService acceptService, ISocialNoticeService noticeService) {
		this.coreMissionDetailMapper = coreMissionDetailMapper;
		this.userService = userService;
		this.corePartnerService = corePartnerService;
		this.acceptService = acceptService;
		this.noticeService = noticeService;
	}

	@Override
	public IPage<MissionDetail> getCoreMissionDetailsByTypePropertyIdWithoutMyMissionAndAcceptedMission(Page<MissionDetail> page, Integer typePropertyId, Boolean isOrderByYouxian, Boolean isOrderByRenqi, Integer userId, DeviceType deviceType, String searchData) {
		Integer deviceTypeInteger = null;
		if (deviceType != null && deviceType != DeviceType.ALL) {
			deviceTypeInteger = deviceType.getValue();
		}
		return coreMissionDetailMapper.selectCoreMissionDetailsByTypePropertyIdWithoutMyMissionAndAcceptedMission(page, typePropertyId, isOrderByYouxian, isOrderByRenqi, userId, deviceTypeInteger, searchData);
	}

	@Override
	public MissionDetail getExistAndPublishedMissionDetailByMissionId(int missionId) {
		MissionDetail md = coreMissionDetailMapper.selectMissionDetailByMissionId(missionId);
		if (md == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "任务ID[%d]不存在", missionId);
		}
		if (md.getStatusPropertyId() != MissionPublishType.PUBLISHED) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "当前任务不可用");
		}
		return md;
	}

	/**
	 * 根据id查找任务详情，且保证这个任务一定是存在且任务状态是已发布状态 MissionPublishType.PUBLISHED，并且在core_mission_rule中这类型任务时可用状态，如果不是这种状态，就抛异常
	 *
	 * @param missionId
	 * @return
	 */
	@Override
	public MissionDetail getExistAndPublishedAndUsableRuleMissionDetailByMissionId(int missionId) {
		MissionDetail md = coreMissionDetailMapper.selectUsableRuleMissionDetailByMissionId(missionId);
		if (md == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "任务ID[%d]不存在", missionId);
		}
		if (md.getStatusPropertyId() != MissionPublishType.PUBLISHED) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "当前任务不可用");
		}
		return md;
	}

	/**
	 * 根据id查找任务详情
	 *
	 * @param missionId
	 * @return
	 */
	@Override
	public MissionDetail getMissionDetailByMissionId(int missionId) {
		return coreMissionDetailMapper.selectMissionDetailByMissionId(missionId);
	}

	/**
	 * 追加任务数量
	 *
	 * @param sysUser
	 * @param missionDetail
	 * @param count         变化的数量
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addMissionCount(SysUser sysUser, MissionDetail missionDetail, int count) {
		//手续费
		int fee = RMBUtil.getIntPrice(new BigDecimal(missionDetail.getPrice() * count * missionDetail.getFeePercent()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP));
		//总花费
		int totalMissionCoin = missionDetail.getPrice() * count + fee;
		if (sysUser.getMissionCoin() < totalMissionCoin) {
			throw new LogicException("任务币余额不足");
		}
		//更新数量
		coreMissionDetailMapper.modifyMissionCount(count, missionDetail.getId());
		//更新手续费
		//更新任务详情手续费
		CoreMissionDetail cmd = new CoreMissionDetail();
		cmd.setId(missionDetail.getId());
		cmd.setFeePrice(missionDetail.getFeePrice() + fee);
		updateById(cmd);
		//任务币变动
		userService.updateUserMissionCoin(sysUser, CurrencyChangeReasonType.PUBLISHED_MISSION_ADD_COUNT, -totalMissionCoin);
		log.info("任务{}添加数量{}，扣除手续费{}元，总扣除{}元", missionDetail.getMissionId(), count, new BigDecimal(fee).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP), RMBUtil.fenToYuan(totalMissionCoin));
	}

	/**
	 * 上调任务出价
	 *
	 * @param sysUser
	 * @param missionDetail
	 * @param price         调整后的价格
	 */
	@Override
	public void addMissionPrice(SysUser sysUser, MissionDetail missionDetail, int price) {
		//手续费
		int fee = RMBUtil.getIntPrice(new BigDecimal((price - missionDetail.getPrice()) * missionDetail.getCount() * missionDetail.getFeePercent()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP));
		//总花费
		int totalMissionCoin = (price - missionDetail.getPrice()) * missionDetail.getCount() + fee;
		if (sysUser.getMissionCoin() < totalMissionCoin) {
			throw new LogicException("任务币余额不足");
		}
		//更新任务详情手续费
		CoreMissionDetail cmd = new CoreMissionDetail();
		cmd.setId(missionDetail.getId());
		cmd.setPrice(price);
		cmd.setFeePrice(missionDetail.getFeePrice() + fee);
		updateById(cmd);
		//任务币变动
		userService.updateUserMissionCoin(sysUser, CurrencyChangeReasonType.PUBLISHED_MISSION_CHANGE_PRICE, -totalMissionCoin);
		log.info("任务{}上调价格为{}，扣除手续费{}元，总扣除{}元", missionDetail.getMissionId(), RMBUtil.fenToYuan(price), RMBUtil.fenToYuan(fee), RMBUtil.fenToYuan(totalMissionCoin));
	}

	/**
	 * 更新所有超过截止时间的任务，将任务更改为截止，并把剩余没做完的任务币退还
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateAllMissionOverDeadline() {
		List<MissionDetail> list = coreMissionDetailMapper.selectAllMissionOverDeadline();
		for (MissionDetail mission : list) {
			UpdateWrapper<CoreMissionAccept> uw = new UpdateWrapper<>();
			uw.lambda().eq(CoreMissionAccept::getMissionId, mission.getMissionId())
					.in(CoreMissionAccept::getProceedPropertyId, MissionProceedType.WAIT_COMMIT, MissionProceedType.IN_REVIEW, MissionProceedType.REJECTED)
					.set(CoreMissionAccept::getProceedPropertyId, MissionProceedType.OVER_DEADLINE)
					.set(CoreMissionAccept::getFinishTime, LocalDateTime.now());
			acceptService.update(uw);
			CoreMission cm = new CoreMission();
			cm.setId(mission.getMissionId());
			cm.setStatusPropertyId(MissionPublishType.OVER_DEADLINE);
			missionService.updateById(cm);
			//将剩余的任务币退回，只退回没做完的任务币，手续费不退
			int price = mission.getPrice() * mission.getCount();
			userService.updateUserMissionCoin(userService.getById(mission.getUserId()), CurrencyChangeReasonType.PUBLISH_MISSION_OVER_DEADLINE, price);
			noticeService.sendNotice(NoticeType.MISSION_OVER_DEADLINE, String.format("您发布的任务<%s>已超过截止日期，剩余%d个任务未完成，%.02f元任务币已退回", mission.getTitle(), mission.getCount(), RMBUtil.fenToYuan(price)), mission.getUserId());
			log.info("任务{}超过截止日期，退回任务币{}元", mission.getMissionId(), RMBUtil.fenToYuan(price));
		}
	}
}
