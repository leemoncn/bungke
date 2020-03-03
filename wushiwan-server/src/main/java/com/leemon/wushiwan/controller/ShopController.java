package com.leemon.wushiwan.controller;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.dto.MyPublishedMission;
import com.leemon.wushiwan.entity.CoreImg;
import com.leemon.wushiwan.entity.CoreMissionDetail;
import com.leemon.wushiwan.dto.ShopDetail;
import com.leemon.wushiwan.enums.CoreImgTypeMission;
import com.leemon.wushiwan.enums.DeviceType;
import com.leemon.wushiwan.enums.MissionPublishType;
import com.leemon.wushiwan.mapper.CoreMissionDetailMapper;
import com.leemon.wushiwan.mapper.ShopMapper;
import com.leemon.wushiwan.mapper.SocialFollowMapper;
import com.leemon.wushiwan.service.ICoreImgService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 我的店铺
 * @author: leemon
 * @date: 2019-07-15
 */
@RestController
@RequestMapping("/shop")
@Slf4j
public class ShopController extends BaseController {

	private final CoreMissionDetailMapper coreMissionDetailMapper;
	private final ShopMapper shopMapper;
	private final ISysUserService userService;
	private final ICoreImgService coreImgService;
	private final SocialFollowMapper followMapper;

	public ShopController(CoreMissionDetailMapper coreMissionDetailMapper, ShopMapper shopMapper, ISysUserService userService, ICoreImgService coreImgService, SocialFollowMapper followMapper) {
		this.coreMissionDetailMapper = coreMissionDetailMapper;
		this.shopMapper = shopMapper;
		this.userService = userService;
		this.coreImgService = coreImgService;
		this.followMapper = followMapper;
	}

	@Data
	private static class ShopMissionItem {
		private Integer id;
		private String imgUrl;
		private String title;
		private Integer price;
		private Boolean isDing;
		private Boolean isBao;
		//剩余单数
		private Integer count;
		private Integer typePropertyId;
		private MissionPublishType missionStatus;
		private Integer mobilePropertyId;
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime deadlineTime;
	}

	@Data
	private static class DetailResponse {
		//发布任务数
		private Long publishedMissionCount;
		//总发单数
		private Long publishedOrderCount;
		//发布任务的完成单数
		private Long finishedOrderCount;
		//被申诉数
		private Long beComplaintCount;
		//接受过的任务数量
		private Long acceptMissionCount;
		//申诉数
		private Long complaintCount;
		//进行中任务
		private List<ShopMissionItem> processMissionList;
		//已下架任务
		private List<ShopMissionItem> offMissionList;
		//是否关注了这个人
		private Boolean isFollowed;
	}

	/**
	 * 查看店铺
	 *
	 * @return
	 */
	@PostMapping("/detail")
	public Object detail(@Valid @RequestBody IdRequest req) {
		Integer userId = req.getId();
		Page<MyPublishedMission> page = new Page<>();
		page.setDesc("publish_time");
		coreMissionDetailMapper.selectMyPublishedMissions(page, userId);
		//我发布的所有任务，包含进行中的和已下架的等等
		List<MyPublishedMission> missionList = page.getRecords();
		//进行中的任务
		List<MyPublishedMission> processMissionList = missionList.stream().filter(myPublishedMission -> myPublishedMission.getStatusPropertyId() == MissionPublishType.PUBLISHED).collect(Collectors.toList());
		//已下架的任务(不包括进行中、审核中、审核驳回的)
		List<MyPublishedMission> offMissionList = missionList.stream().filter(myPublishedMission ->
				myPublishedMission.getStatusPropertyId() != MissionPublishType.PUBLISHED &&
						myPublishedMission.getStatusPropertyId() != MissionPublishType.REVIEWING &&
						myPublishedMission.getStatusPropertyId() != MissionPublishType.REVIEW_REJECT).collect(Collectors.toList());
		DetailResponse res = new DetailResponse();

		BiConsumer<List<MyPublishedMission>, List<ShopMissionItem>> consumer = (publishedMissionList, list) ->
				publishedMissionList.forEach(mission -> {
					ShopMissionItem item = new ShopMissionItem();
					item.setId(mission.getMissionId());
					item.setTitle(mission.getTitle());
					item.setPrice(mission.getPrice());
					item.setDeadlineTime(mission.getDeadlineTime());
					item.setMobilePropertyId(mission.getMobilePropertyId());
					LocalDateTime topTime = mission.getTopEndTime();
					if (topTime != null && topTime.isAfter(LocalDateTime.now())) {
						item.setIsDing(true);
					} else {
						item.setIsDing(false);
					}
					SysUser publisher = userService.getById(mission.getUserId());
					item.setIsBao(publisher.getDeposit() != null && publisher.getDeposit() > 0);
					item.setCount(mission.getCount());
					item.setTypePropertyId(mission.getTypePropertyId());
					item.setMissionStatus(mission.getStatusPropertyId());

					//审核图样
					List<CoreImg> sampleImgList = coreImgService.queryUserAllImg(mission.getMissionId(), mission.getUserId(), CoreImgTypeMission.SAMPLE.ordinal());

					if (sampleImgList.size() > 0) {
						item.setImgUrl(sampleImgList.get(0).getPath());
					}
					list.add(item);
				});

		List<ShopMissionItem> processList = new ArrayList<>();
		consumer.accept(processMissionList, processList);

		List<ShopMissionItem> offList = new ArrayList<>();
		consumer.accept(offMissionList, offList);

		res.setProcessMissionList(processList);
		res.setOffMissionList(offList);

		Supplier<Stream<MyPublishedMission>> getValidMissionStream = () -> missionList.stream().filter(value -> value.getStatusPropertyId() != MissionPublishType.REVIEWING &&
				value.getStatusPropertyId() != MissionPublishType.REVIEW_REJECT);


		res.setPublishedMissionCount(getValidMissionStream.get().count());
		res.setPublishedOrderCount(getValidMissionStream.get().mapToInt(CoreMissionDetail::getCount).summaryStatistics().getSum());

		ShopDetail shopDetail = shopMapper.selectShopDetail(userId);
		BeanUtils.copyProperties(shopDetail, res);

		res.setIsFollowed(followMapper.selectIfFollowedUser(UserUtil.getSysUserId(), userId));

		return res;
	}
}
