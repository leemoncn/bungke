package com.leemon.wushiwan.service.impl;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreMission;
import com.leemon.wushiwan.entity.CoreMissionAccept;
import com.leemon.wushiwan.entity.CoreMissionDetail;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.MissionPublishType;
import com.leemon.wushiwan.mapper.CoreMissionAcceptMapper;
import com.leemon.wushiwan.mapper.CoreMissionMapper;
import com.leemon.wushiwan.service.ICoreMissionAcceptService;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import com.leemon.wushiwan.service.ICoreMissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CoreMissionServiceImpl extends ServiceImpl<CoreMissionMapper, CoreMission> implements ICoreMissionService {

	private final ICoreMissionAcceptService acceptService;
	private final CoreMissionAcceptMapper acceptMapper;
	@Autowired
	private ICoreMissionDetailService detailService;
	private final ISysUserService userService;

	public CoreMissionServiceImpl(ICoreMissionAcceptService acceptService, CoreMissionAcceptMapper acceptMapper, ISysUserService userService) {
		this.acceptService = acceptService;
		this.acceptMapper = acceptMapper;
		this.userService = userService;
	}

	/**
	 * 任务下架
	 *
	 * @param missionDetail
	 * @param user
	 * @return 退回的任务币数量
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int offMission(MissionDetail missionDetail, SysUser user) {
		//任务下架需要先将在进行中的任务变成已完成（待提交的和待审核的）
		List<CoreMissionAccept> list = acceptMapper.selectInProgressAcceptList(missionDetail.getMissionId(), user.getId());
		list.forEach(cma -> acceptService.reviewMission(cma.getId(), true, null, null, false, null));
		CoreMission cm = new CoreMission();
		cm.setStatusPropertyId(MissionPublishType.OFF);
		cm.setId(missionDetail.getMissionId());
		updateById(cm);
		//将用户任务剩余任务币退回
		CoreMissionDetail cmd = detailService.getById(missionDetail.getId());
		//进行中以及在审核中的任务、不合格但是没超过12小时的任务，都将认为是任务完成
		int count = missionDetail.getAcceptableMissionCount();
		int price = cmd.getPrice();
		int priceTotalFen = count * price;
		userService.updateUserMissionCoin(user, CurrencyChangeReasonType.OFF_MISSION, priceTotalFen);
		return priceTotalFen;
	}
}
