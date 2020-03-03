package com.leemon.wushiwan.controller;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.dto.ExplainData;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.dto.MyAcceptMission;
import com.leemon.wushiwan.dto.MyPublishedMission;
import com.leemon.wushiwan.entity.*;
import com.leemon.wushiwan.enums.*;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.enums.base.PropertyType;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.*;
import com.leemon.wushiwan.service.*;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.PageUtil;
import com.leemon.wushiwan.util.RMBUtil;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.CreateMissionRequest;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import com.leemon.wushiwan.vo.MissionAcceptVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 任务发布管理
 * @author: leemon
 * @date: 2019-05-07
 */
@RestController
@RequestMapping("/core-mission")
@Slf4j
public class CoreMissionController extends BaseController<CoreMission> {

	private final ICoreMissionService coreMissionService;
	private final ICoreMissionDetailService coreMissionDetailService;
	private final CoreMissionDetailMapper coreMissionDetailMapper;
	private final ISysPropertyService sysPropertyService;
	private final ICoreMissionRuleService coreMissionRuleService;
	private final ICoreImgService coreImgService;
	private final ICorePartnerService corePartnerService;
	private final ISysUserService sysUserService;
	private final ICoreMissionAcceptService coreMissionAcceptService;
	private final CoreMissionAcceptMapper coreMissionAcceptMapper;
	private final CoreMissionMapper coreMissionMapper;
	private final ISocialNoticeService noticeService;
	private final ISocialReviewChatService chatService;
	private final SocialFollowMapper followMapper;
	private final ICoreMissionDetailStepService stepService;
	private final CoreMissionDetailStepMapper stepMapper;
	private final IFinancialTradeService tradeService;
	private final QiniuKodoService kodoService;
	private final Config config;

	@Autowired
	public CoreMissionController(ICoreMissionService coreMissionService, ICoreMissionDetailService coreMissionDetailService, ISysPropertyService sysPropertyService, ICoreMissionRuleService coreMissionRuleService, ICoreImgService coreImgService, ICorePartnerService corePartnerService, ISysUserService sysUserService, Config config, CoreMissionDetailMapper coreMissionDetailMapper, ICoreMissionAcceptService coreMissionAcceptService, CoreMissionAcceptMapper coreMissionAcceptMapper, CoreMissionMapper coreMissionMapper, ISocialNoticeService noticeService, ISocialReviewChatService chatService, SocialFollowMapper followMapper, ICoreMissionDetailStepService stepService, CoreMissionDetailStepMapper stepMapper, IFinancialTradeService tradeService, QiniuKodoService kodoService, Config config1) {
		this.coreMissionService = coreMissionService;
		this.coreMissionDetailService = coreMissionDetailService;
		this.sysPropertyService = sysPropertyService;
		this.coreMissionRuleService = coreMissionRuleService;
		this.coreImgService = coreImgService;
		this.corePartnerService = corePartnerService;
		this.sysUserService = sysUserService;
		this.coreMissionDetailMapper = coreMissionDetailMapper;
		this.coreMissionAcceptService = coreMissionAcceptService;
		this.coreMissionAcceptMapper = coreMissionAcceptMapper;
		this.coreMissionMapper = coreMissionMapper;
		this.noticeService = noticeService;
		this.chatService = chatService;
		this.followMapper = followMapper;
		this.stepService = stepService;
		this.stepMapper = stepMapper;
		this.tradeService = tradeService;
		this.kodoService = kodoService;
		this.config = config1;
	}

	private CoreMissionRule validRequest(CreateMissionRequest req) {
		PropertyType missionType = sysPropertyService.getPropertyTypeById(req.getTypePropertyId());
		if (missionType != PropertyType.MISSION_TYPE) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "typePropertyId不是任务类型");
		}
		PropertyType mobileType = sysPropertyService.getPropertyTypeById(req.getMobilePropertyId());
		if (mobileType != PropertyType.DEVICE_TYPE) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "mobilePropertyId不是设备类型");
		}
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(req.getDeadlineTime())) {
			throw new LogicException("截止时间需要晚于当前时间");
		}
		CoreMissionRule missionRule = coreMissionRuleService.getCoreMissionRuleByMissionTypePropertyId(req.getTypePropertyId());
		if (missionRule == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "CoreMissionRule不存在,typePropertyId = %s", req.getTypePropertyId());
		}
		if (req.getPrice() < missionRule.getMinPrice()) {
			throw new LogicException("小于任务规则的最低出价");
		}
		if (req.getCount() < missionRule.getMinCount()) {
			throw new LogicException("小于任务规则的最低任务数量");
		}
		if (req.getSampleImgList().size() > missionRule.getVerifyImgCount()) {
			throw new LogicException("大于任务规则的最多审核图样数量");
		}
		if (req.getExplainList() != null && req.getExplainList().size() > 30) {
			throw new LogicException("操作步骤过多，最多30个步骤");
		}
		//任务持续时间
		Duration duration = Duration.between(LocalDateTime.now(), req.getDeadlineTime());
		int minHour = config.getMissionReviewHour() * 2;
		if (duration.getSeconds() < minHour * 60 * 60) {
			throw new LogicException("任务截止时间不得小于" + minHour + "小时");
		}
		//TODO 置顶日期等等
		return missionRule;
	}

	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/create")
	public Object createMission(@RequestBody @Valid CreateMissionRequest req) {
		CoreMissionRule missionRule = validRequest(req);
		if (req.getMissionId() != null) {//重发任务,先将之前的任务删除掉
			MissionDetail oldMission = coreMissionDetailService.getMissionDetailByMissionId(req.getMissionId());
			if (oldMission.getStatusPropertyId() != MissionPublishType.REVIEW_REJECT &&
					oldMission.getStatusPropertyId() != MissionPublishType.OFF &&
					oldMission.getStatusPropertyId() != MissionPublishType.OVER_DEADLINE) {
				throw new LogicException("只有已下架或者审核驳回或者超过截止时间的任务才可重新发布");
			}
			coreMissionService.removeById(oldMission.getMissionId());
			coreMissionDetailService.removeById(oldMission.getId());
			QueryWrapper<CoreMissionDetailStep> stepQw = new QueryWrapper<>();
			stepQw.lambda().eq(CoreMissionDetailStep::getMissionDetailId, oldMission.getId());
			List<CoreMissionDetailStep> oldStep = stepService.list(stepQw);
			List<Integer> stepIdList = new ArrayList<>();
			for (CoreMissionDetailStep step : oldStep) {
				stepIdList.add(step.getId());
			}
			//删除数据库中的图片记录，但不删除磁盘中的图片
			//TODO 添加定时任务，删除一个月不用的磁盘上的图片
			QueryWrapper<CoreImg> qw = new QueryWrapper<>();
			//TODO 这里要测试一下，看sql是否正确
			qw.lambda().eq(CoreImg::getUserId, oldMission.getUserId())
					.and(w -> w.and(
							ww -> ww.eq(CoreImg::getDataId, oldMission.getMissionId())
									.eq(CoreImg::getType, CoreImgTypeMission.SAMPLE.ordinal())
							)
									.or()
									.and(
											ww -> {
												if (oldStep.size() == 0) {
													return ww;
												}
												return ww.in(CoreImg::getDataId, stepIdList)
														.eq(CoreImg::getType, CoreImgTypeMission.EXPLAIN.ordinal());
											}
									)
					);
			coreImgService.remove(qw);
			if (stepIdList.size() > 0) {
				stepService.removeByIds(stepIdList);
			}
		}

		SysUser user = UserUtil.getSysUser();
		CorePartner currentPartner = corePartnerService.getById(user.getPartnerId());
		if (currentPartner == null) {
			throw new LogicException(ErrorCode.SYS_ERROR, "用户partner为空，用户id = %d", user.getId());
		}
		//手续费
		int fee = RMBUtil.getIntPrice(RMBUtil.fenToYuan(req.getPrice() * req.getCount() * currentPartner.getFeePercent()));
		//先进位，保留两位小数
		if (fee < currentPartner.getMinFeePrice()) {
			fee = currentPartner.getMinFeePrice();
		}
		//总花费
		int totalMissionCoin = req.getPrice() * req.getCount() + fee;
		if (user.getMissionCoin() < totalMissionCoin) {
			throw new LogicException("任务币余额不足");
		}
		//创建任务
		CoreMissionDetail coreMissionDetail = new CoreMissionDetail();
		BeanUtils.copyProperties(req, coreMissionDetail);
		coreMissionDetail.setPublishCount(req.getCount());
		coreMissionDetail.setUserId(user.getId());
		coreMissionDetail.setMissionRuleId(missionRule.getId());
		coreMissionDetail.setFeePercent(currentPartner.getFeePercent());
		coreMissionDetail.setFeePrice(fee);
		//保存mission detail
		coreMissionDetailService.save(coreMissionDetail);
		//保存mission
		CoreMission coreMission = new CoreMission();
		coreMission.setMissionDetailId(coreMissionDetail.getId());
		coreMission.setUserId(user.getId());
		coreMission.setPublishTime(LocalDateTime.now());
		//新提交的任务需要后台管理员审核才用
		coreMission.setStatusPropertyId(MissionPublishType.REVIEWING);
		coreMission.setTopEndTime(null);
		coreMissionService.save(coreMission);
		//任务币变动
		sysUserService.updateUserMissionCoin(user, CurrencyChangeReasonType.PUBLISH_MISSION, -totalMissionCoin);
		//保存image
		//TODO 图片的大小应该写在数据库配置里面
		coreImgService.saveImgListToDB(user.getId(), coreMission.getId(), CoreImgTypeMission.SAMPLE, req.getSampleImgList());
		//保存任务执行步骤
		for (ExplainData e : req.getExplainList()) {
			CoreMissionDetailStep step = new CoreMissionDetailStep();
			if (!Strings.isNullOrEmpty(e.getImg())) {
				step.setHaveImg(true);
			} else {
				step.setHaveImg(false);
			}
			step.setMissionDetailId(coreMissionDetail.getId());
			step.setText(e.getText());
			stepService.save(step);
			if (!Strings.isNullOrEmpty(e.getImg())) {
				coreImgService.saveImgListToDB(user.getId(), step.getId(), CoreImgTypeMission.EXPLAIN, Collections.singletonList(e.getImg()));
			}
		}
		kodoService.deleteAllFileUploadedRedisKey(req.getSampleKey());
		kodoService.deleteAllFileUploadedRedisKey(req.getExplainKey());
		return UserUtil.getSysUser();
	}

	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/republish")
	public Object republishMission(@RequestBody @Valid CreateMissionRequest req) {
		CoreMissionRule missionRule = validRequest(req);

		SysUser user = UserUtil.getSysUser();
//		CorePartner currentPartner = corePartnerService.getById(user.getPartnerId());
//		if (currentPartner == null) {
//			throw new LogicException(ErrorCode.SYS_ERROR, "用户partner为空，用户id = %d", user.getId());
//		}
//		//手续费
//		int fee = req.getPrice() * req.getCount() * currentPartner.getFeePercent() / 100;
//		//总花费
//		int totalMissionCoin = req.getPrice() * req.getCount() + fee;
//		if (user.getMissionCoin() < totalMissionCoin) {
//			throw new LogicException("任务币余额不足");
//		}
		//创建任务
		return null;
	}


	@Data
	@EqualsAndHashCode(callSuper = true)
	private static class MissionListRequest extends BaseEntity {
		private Integer typePropertyId;
		//		"最新", "优先", "人气", "设备类型（苹果或者安卓）"
		@NotNull
		private Integer orderType;
		//设备类型，0是未知，1是安卓，2是苹果
		private DeviceType deviceType;
		//搜索关键字
		private String searchData;
	}

	@Data
	private static class MissionListResponse {
		private Integer id;
		private String imgUrl;
		private String title;
		private Integer price;
		private Integer missionTypePropertyId;
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime deadlineTime;
		private Boolean isDing;
		private Boolean isBao;
		private Integer mobilePropertyId;
	}

	@RequestMapping(value = "/clist")
	public Object missionListData(@RequestBody @Valid MissionListRequest req) {
		Page<MissionDetail> page = new Page<>(req.getPageNo(), req.getPageSize());
		boolean isOrderByYouxian = false, isOrderByRenqi = false;
		DeviceType orderDeviceType = null;
		switch (req.getOrderType()) {
			case 0://最新
				page.setDesc("topEndTime", "publishTime");
				break;
			case 1://优先(最近24小时内完成任务数量为依据),这个和人气的排序方式不一致，要看mybatis里面的sql语句（ordercount不一样）
				isOrderByYouxian = true;
				page.setDesc("topEndTime", "orderCount", "publishTime");
				break;
			case 2://人气(最近24小时内接受任务的数量为依据)
				isOrderByRenqi = true;
				page.setDesc("topEndTime", "orderCount", "publishTime");
				break;
			case 3://设备类型（苹果或者安卓）,在sql里面直接限制住
				orderDeviceType = req.getDeviceType();
				page.setDesc("topEndTime", "publishTime");
				break;
			default:
		}

		coreMissionDetailService.getCoreMissionDetailsByTypePropertyIdWithoutMyMissionAndAcceptedMission(page, req.getTypePropertyId(), isOrderByYouxian, isOrderByRenqi, UserUtil.getSysUserId(), orderDeviceType, req.getSearchData());
		List<MissionListResponse> list = new ArrayList<>();
		List<MissionDetail> missionDetailList = page.getRecords();

		for (MissionDetail coreMissionDetail : missionDetailList) {
			MissionListResponse res = new MissionListResponse();
			res.setId(coreMissionDetail.getMissionId());
			res.setTitle(coreMissionDetail.getTitle());
			res.setPrice(coreMissionDetail.getPrice());
			res.setMissionTypePropertyId(coreMissionDetail.getTypePropertyId());
			res.setDeadlineTime(coreMissionDetail.getDeadlineTime());
			res.setMobilePropertyId(coreMissionDetail.getMobilePropertyId());
			LocalDateTime topTime = coreMissionDetail.getTopEndTime();
			if (topTime != null && topTime.isAfter(LocalDateTime.now())) {
				res.setIsDing(true);
			} else {
				res.setIsDing(false);
			}
			SysUser publisher = sysUserService.getById(coreMissionDetail.getUserId());
			res.setIsBao(publisher.getDeposit() != null && publisher.getDeposit() > 0);

			//审核图样
			List<CoreImg> sampleImgList = coreImgService.queryUserAllImg(coreMissionDetail.getMissionId(), coreMissionDetail.getUserId(), CoreImgTypeMission.SAMPLE.ordinal());

			if (sampleImgList.size() > 0) {
				res.setImgUrl(sampleImgList.get(0).getPath());
			}
			list.add(res);
		}
		return PageUtil.convertPageRecords(page, list);
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	private static class MissionDetailResponse extends BaseEntity {
		/**
		 * 发布任务的类型，在属性表中的id
		 */
		private Integer typePropertyId;

		/**
		 * 发布人id
		 */
		private Integer userId;

		/**
		 * 属性表里面的支持设备类型
		 */
		private Integer mobilePropertyId;

		/**
		 * 标题，12字以内
		 */
		private String title;

		/**
		 * 截止时间
		 */
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime deadlineTime;

		/**
		 * 出价，单位分
		 */
		private Integer price;

		/**
		 * 任务数量
		 */
		private Integer count;

		/**
		 * 链接
		 */
		private String url;

		/**
		 * 文字验证
		 */
		private String textVerify;

		/**
		 * 备注
		 */
		private String remark;
		//审核图样的图片
		private List<String> sampleImgList;

		//操作说明的图片
		private List<ExplainData> explainList;
		//置顶截止时间
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime topEndTime;
		//任务发布人的名字
		private String publishUserName;
		//任务发布人的头像地址
		private String publishUserImgUrl;
		//任务发布人交的保证金
		private Integer deposit;
		//是否关注了这个任务发布人
		private Boolean isFollowed;
	}

	@RequestMapping(value = "/mission-detail")
	public Object missionDetail(@RequestBody @Valid IdRequest req) {
		MissionDetail md = coreMissionDetailService.getExistAndPublishedMissionDetailByMissionId(req.getId());
		SysUser publishUser = sysUserService.getById(md.getUserId());
		if (publishUser == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "发布人ID[%d]不存在", md.getUserId());
		}
		MissionDetailResponse res = new MissionDetailResponse();
		BeanUtils.copyProperties(md, res);
		//审核图样
		List<CoreImg> sampleImgList = coreImgService.queryUserAllImg(md.getMissionId(), md.getUserId(), CoreImgTypeMission.SAMPLE.ordinal());
		//操作说明
		List<ExplainData> explainDataList = stepMapper.selectExplainData(md.getUserId(), CoreImgTypeMission.EXPLAIN.ordinal(), md.getId());
		res.setTopEndTime(md.getTopEndTime());
		res.setSampleImgList(sampleImgList.stream().map(CoreImg::getPath).collect(Collectors.toList()));
		res.setExplainList(explainDataList);
		res.setPublishUserName(publishUser.getNickname());
		res.setPublishUserImgUrl(publishUser.getHeadImgUrl());
		res.setDeposit(publishUser.getDeposit());
		res.setIsFollowed(followMapper.selectIfFollowedUser(UserUtil.getSysUserId(), publishUser.getId()));
		return res;
	}

	/**
	 * 接取任务后，count数量不变，只有完成后，count才会改变
	 * 可接受的任务数量为，剩余任务数量-正在进行中的任务数量-审核中的任务数-不合格的任务但还没有超过12小时的重新上传期的任务书
	 *
	 * @param idRequest
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/accept-mission")
	public Object acceptMission(@RequestBody @Valid IdRequest idRequest) {
		MissionDetail md = coreMissionDetailService.getExistAndPublishedAndUsableRuleMissionDetailByMissionId(idRequest.getId());
		if (coreMissionAcceptService.isAcceptMission(md.getMissionId(), UserUtil.getSysUserId())) {
			throw new LogicException("已有当前任务，不可重复接取");
		}
		if (md.getUserId().equals(UserUtil.getSysUserId())) {
			throw new LogicException("不能接取自己的任务");
		}
		if (md.getAcceptableMissionCount() <= 0) {//可接受的任务数量为，剩余任务数量-正在进行中的任务数量-审核中的任务数
			throw new LogicException("当前任务数量不足");
		}
		//任务剩余的可以完成的时间（现在时间-任务截止时间）
		Duration missionCanFinishDuration = Duration.between(LocalDateTime.now(), md.getDeadlineTime());
		Integer minHour = config.getMissionReviewHour() * 2;
		if (missionCanFinishDuration.getSeconds() < minHour * 60 * 60) {
			throw new LogicException("任务剩余时间不足" + minHour + "小时,无法接取");
		}
		//用户接取任务无需更新剩余任务数量，用户完成才更新
		coreMissionAcceptService.acceptMission(idRequest.getId(), UserUtil.getSysUserId(), md.getUserId());
		return null;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	private static class MyMissionAcceptListRequest extends BaseEntity {
		//		0-全部 1-待提交 2-审核中 3-不合格 4-已完成
		@NotNull
		private Integer orderType;
	}

	@RequestMapping(value = "/my-mission-accept-list")
	public Object myMissionAcceptList(@RequestBody @Valid MyMissionAcceptListRequest req) {
		Page<MyAcceptMission> page = new Page<>(req.getPageNo(), req.getPageSize());
		page.setDesc("acceptTime");
		if (req.getOrderType() == 0) {//全部
			coreMissionAcceptMapper.selectMyAcceptMissionOrderByProceedPropertyId(page, UserUtil.getSysUserId(), null);
		} else {//有排序
			MissionProceedType type = MissionProceedType.values()[req.getOrderType() - 1];
			coreMissionAcceptMapper.selectMyAcceptMissionOrderByProceedPropertyId(page, UserUtil.getSysUserId(), type.getValue());
		}
		return page;
	}

	@RequestMapping(value = "/my-mission-publish-list")
	public Object myMissionPublishList(@RequestBody BaseEntity req) {
		Page<MyPublishedMission> page = new Page<>(req.getPageNo(), req.getPageSize());
		page.setDesc("waitingReviewCount", "publish_time");
		coreMissionDetailMapper.selectMyPublishedMissions(page, UserUtil.getSysUserId());
		return page;
	}

	@Data
	private static class FinishMissionRequest {
		@NotNull
		private Integer missionId;
		private String textVerify;
		@NotBlank
		private String key;
	}

	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/finish-mission")
	public Object finishMission(@RequestBody @Valid FinishMissionRequest req) {
		SysUser commitUser = UserUtil.getSysUser();
		CoreMissionAccept cma = coreMissionAcceptService.getCanCommitMission(commitUser.getId(), req.getMissionId());
		if (cma == null) {
			throw new LogicException("当前已接受任务不存在");
		}
		MissionDetail md = coreMissionDetailService.getExistAndPublishedMissionDetailByMissionId(req.getMissionId());
		if (Strings.isNullOrEmpty(md.getTextVerify()) && !Strings.isNullOrEmpty(req.getTextVerify())) {
			throw new LogicException("此任务不需要提交文字验证");
		}

		if (!Strings.isNullOrEmpty(md.getTextVerify()) && Strings.isNullOrEmpty(req.getTextVerify())) {
			throw new LogicException("此任务需要提交文字验证");
		}
		//有可能是用户被判为不合格后的再次提交，所以要先把之前的验证图删掉
		QueryWrapper<CoreImg> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreImg::getDataId, cma.getId())
				.eq(CoreImg::getType, CoreImgTypeMission.VERIFY_IMG.ordinal())
				.eq(CoreImg::getUserId, commitUser.getId());
		coreImgService.remove(qw);
		coreMissionAcceptMapper.updateMissionTextVerify(commitUser.getId(), req.getMissionId(), req.getTextVerify());
		List<String> pathList = kodoService.deleteAllFileUploadedRedisKey(req.getKey());
		coreImgService.saveImgListToDB(commitUser.getId(), cma.getId(), CoreImgTypeMission.VERIFY_IMG, pathList);
		String msg = String.format("用户<%s>已提交任务<%s>，请尽快审核", commitUser.getNickname(), md.getTitle());
		noticeService.sendNotice(NoticeType.COMMIT_MISSION, msg, md.getUserId());
		chatService.addNewChat(null, md, null, commitUser.getId(), md.getUserId(), cma.getId(), ChatStatusType.CommitMission.getTitle());
		return null;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	private static class MissionReviewDetailResponse extends BaseEntity {
		/**
		 * 发布任务的类型，在属性表中的id
		 */
		private Integer typePropertyId;

		/**
		 * 标题，12字以内
		 */
		private String title;

		/**
		 * 截止时间
		 */
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime deadlineTime;

		/**
		 * 出价，单位分
		 */
		private Integer price;

		/**
		 * 链接
		 */
		private String url;

		/**
		 * 文字验证
		 */
		private String textVerify;

		/**
		 * 备注
		 */
		private String remark;
		//审核图片
		private List<String> uploadImgList;
		//置顶截止时间
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime topEndTime;
		/**
		 * 用户提交的文字验证
		 */
		private String textVerifyFromUser;

		/**
		 * 任务执行情况
		 */
		@TableField("proceed_property_id")
		private MissionProceedType proceedPropertyId;

		private Integer acceptId;
		private Integer missionId;
		private Integer publishUserId;
		//任务发布人的名字
		private String publishUserName;
		private Integer acceptUserId;
		private LocalDateTime finishTime;
		/**
		 * 当前任务如果是不合格状态，是否可以投诉
		 */
		private Boolean canComplaint;

		/**
		 * 申诉结果，如果没申诉过或者平台还未确定申诉结果，为null
		 */
		private Boolean complaintResult;

		private List<ReviewChatResponse> chatList;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	private static class ReviewChatResponse extends SocialReviewChat {
		private List<CoreImg> imgList;
		private String chatTime;
		private String fromUserHeadImg;
		private String fromUserName;
	}

	@Data
	private static class ReviewMissionDetailRequest {
		private Integer id;//accept id
		private Integer missionId;
	}

	@RequestMapping(value = "/review-mission-detail")
	public Object reviewMissionDetail(@RequestBody @Valid ReviewMissionDetailRequest req) {
		MissionAcceptVO cma = null;
		if (req.getId() != null) {
			cma = coreMissionAcceptService.getCoreMissionAcceptById(req.getId());
		}
		if (req.getMissionId() != null) {//获取最新提交的任务
			Integer needReviewAcceptId = coreMissionAcceptMapper.getOneNeedReviewAccept(req.getMissionId());
			if (needReviewAcceptId == null) {
				throw new LogicException("所有任务审核完成");
			}
			cma = coreMissionAcceptService.getCoreMissionAcceptById(needReviewAcceptId);
		}
		if (cma == null) {
			throw new LogicException("当前已接受任务不存在");
		}
		MissionDetail md = coreMissionDetailService.getExistAndPublishedMissionDetailByMissionId(cma.getMissionId());
		MissionReviewDetailResponse res = new MissionReviewDetailResponse();
		BeanUtils.copyProperties(md, res);
		res.setId(cma.getId());
		res.setMissionId(md.getMissionId());
		//审核图样
		List<CoreImg> uploadImgList = coreImgService.queryUserAllImg(cma.getId(), cma.getAcceptUserId(), CoreImgTypeMission.VERIFY_IMG.ordinal());
		res.setTopEndTime(md.getTopEndTime());
		res.setUploadImgList(uploadImgList.stream().map(CoreImg::getPath).collect(Collectors.toList()));
		res.setTextVerifyFromUser(cma.getTextVerify());
		res.setProceedPropertyId(cma.getProceedPropertyId());
		res.setAcceptId(cma.getId());
		res.setFinishTime(cma.getFinishTime());
		res.setPublishUserId(md.getUserId());
		SysUser publishUser = sysUserService.getById(md.getUserId());
		res.setPublishUserName(publishUser.getNickname());
		res.setAcceptUserId(cma.getAcceptUserId());
		res.setCanComplaint(cma.getCanComplaint());
		res.setComplaintResult(cma.getComplaintResult());

		//和商家的聊天记录
		List<SocialReviewChat> socialReviewChatList = chatService.getAllChat(cma.getId());
		Collections.reverse(socialReviewChatList);
		List<ReviewChatResponse> chatList = new ArrayList<>();
		socialReviewChatList.forEach(socialReviewChat -> {
			ReviewChatResponse r = new ReviewChatResponse();
			BeanUtils.copyProperties(socialReviewChat, r);
			r.setImgList(coreImgService.queryUserAllImg(socialReviewChat.getId(), socialReviewChat.getFromUserId(), CoreImgTypeMission.REVIEW_CHAT.ordinal()));
			r.setChatTime(getChatTime(socialReviewChat.getCreateTime()));
			Integer fromUserId = socialReviewChat.getFromUserId();
			if (fromUserId != null) {
				SysUser fromUser = sysUserService.getById(fromUserId);
				r.setFromUserHeadImg(fromUser.getHeadImgUrl());
				r.setFromUserName(fromUser.getNickname());
			}
			chatList.add(r);
		});

		res.setChatList(chatList);
		return res;
	}

	private String getChatTime(LocalDateTime ldt) {
		LocalDateTime now = LocalDateTime.now();
		String timeStr = ldt.format(DateTimeFormatter.ofPattern("HH:mm"));
		if (now.toLocalDate().equals(ldt.toLocalDate())) {
			return timeStr;
		}
		if (now.toLocalDate().minusDays(1).equals(ldt.toLocalDate())) {
			return "昨天 " + timeStr;
		}
		return ldt.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
	}

	@Data
	private static class ReviewMissionRequest {
		@NotNull
		private Boolean result;
		@NotNull
		@Min(1)
		private Integer missionAcceptId;
		//如果审核不合格，填原因
		private String reason;
		private String key;
	}

	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/review-mission")
	public Object reviewMission(@RequestBody @Valid ReviewMissionRequest req) {
		if (!req.getResult()) {
			if (Strings.isNullOrEmpty(req.getReason())) {
				throw new LogicException("请填写不合格原因");
			}
		}
		CoreMissionAccept cma = coreMissionAcceptService.getById(req.getMissionAcceptId());
		if (cma == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "任务接取记录不存在");
		}
		if (cma.getProceedPropertyId() != MissionProceedType.IN_REVIEW) {
			throw new LogicException("当前任务不能审核");
		}
		coreMissionAcceptService.reviewMission(req.getMissionAcceptId(), req.getResult(), req.getReason(), req.getKey(), false, null);
		return null;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	private static class ReviewMissionListRequest extends BaseEntity {
		@NotNull
		private Integer missionId;
		@NotNull
		private Integer proceedPropertyId;
	}

	@Data
	public static class ReviewMissionListResponse {
		private Integer acceptId;
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime uploadTime;
		private Integer uploadUserId;
		private String uploadUserName;
		private Integer missionId;
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime publishTime;
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime topEndTime;

		/**
		 * 发布任务的类型，在属性表中的id
		 */
		@TableField("type_property_id")
		private Integer typePropertyId;

		/**
		 * 标题，12字以内
		 */
		@TableField("title")
		private String title;

		/**
		 * 截止时间
		 */
		@TableField("deadline_time")
		@JSONField(format = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime deadlineTime;

		/**
		 * 出价，单位分
		 */
		@TableField("price")
		private Integer price;

		/**
		 * 任务数量
		 */
		@TableField("count")
		private Integer count;

		/**
		 * 链接
		 */
		@TableField("url")
		private String url;

		/**
		 * 文字验证
		 */
		@TableField("text_verify")
		private String textVerify;

		/**
		 * 备注
		 */
		@TableField("remark")
		private String remark;

		/**
		 * 任务执行情况
		 */
		@TableField("proceed_property_id")
		private MissionProceedType proceedPropertyId;
	}

	@RequestMapping(value = "/review-mission-list")
	public Object reviewMissionList(@RequestBody @Valid ReviewMissionListRequest req) {
		Page<ReviewMissionListResponse> page = new Page<>(req.getPageNo(), req.getPageSize());
		page.setAscs(Arrays.asList("proceed_property_id", "accept_time"));
		coreMissionAcceptMapper.selectReviewMissionList(page, req.getMissionId(), req.getProceedPropertyId(), UserUtil.getSysUserId());
		return page;
	}

	@Data
	private static class OffMissionResponse {
		SysUser user;
		int refund;
	}

	/**
	 * 任务下架
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/off-mission")
	public Object offMission(@RequestBody @Valid IdRequest req) {
		MissionDetail md = coreMissionDetailService.getMissionDetailByMissionId(req.getId());
		if (md == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "当前任务ID不存在");
		}
		if (md.getStatusPropertyId() != MissionPublishType.PUBLISHED) {
			throw new LogicException("只有已发布的任务可以下架");
		}
		SysUser user = UserUtil.getSysUser();
		int refund = coreMissionService.offMission(md, user);
		OffMissionResponse res = new OffMissionResponse();
		res.setRefund(refund);
		res.setUser(user);
		return res;
	}

	@Data
	private static class RechargeMissionCoinRequest {
		@NotNull
		@Min(100)
		private Integer amount;
	}

	/**
	 * 充值任务币
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/recharge")
	public Object rechange(@RequestBody @Valid RechargeMissionCoinRequest req, @RequestHeader(value = "isApp") Boolean isApp) {
		Integer amount = req.getAmount();
		return tradeService.startPay(UserUtil.getSysUserId(), amount, PayType.RechargeMissionCoin, "", isApp);
	}

	/**
	 * 提现任务币
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/withdrawal-mission-coin")
	public Object withdrawalMissionCoin(@RequestBody @Valid RechargeMissionCoinRequest req) {
		Integer amount = req.getAmount();
		sysUserService.updateUserMissionCoin(UserUtil.getSysUser(), CurrencyChangeReasonType.WITHDRAWAL_MISSION_COIN, -amount);
		return UserUtil.getSysUser();
	}

	/**
	 * 提现收入
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/withdrawal-earning")
	public Object withdrawalEarning(@RequestBody @Valid RechargeMissionCoinRequest req) {
		Integer amount = req.getAmount();
		sysUserService.updateUserEarning(UserUtil.getSysUser(), CurrencyChangeReasonType.WITHDRAWAL_EARNING, -amount);
		return UserUtil.getSysUser();
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreMission
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreMission coreMission) {
		Page<MissionDetail> page = new Page<>(coreMission.getPageNo(), coreMission.getPageSize());
		page.setDesc("publish_time");
		List<MissionDetail> list = coreMissionDetailMapper.searchMissionDetail(page, coreMission.getStatusPropertyId() == null ? null : coreMission.getStatusPropertyId().getValue(), coreMission.getUserId());
		page.setRecords(list);
		return page;
	}

	/**
	 * 保存
	 *
	 * @param coreMission
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreMission coreMission) {
		if (coreMission.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		coreMissionService.saveOrUpdate(coreMission);
		return null;
	}

	@Data
	private static class MissionEditRequest {
		@NotNull
		private Integer id;
		private MissionPublishType statusPropertyId;
		/**
		 * 如果审核驳回，需要回传一个原因
		 */
		private String reason;
	}

	/**
	 * 编辑
	 *
	 * @param req
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@PreAuthorize("hasPermission('core_mission_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid MissionEditRequest req) {
		if (req.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		MissionDetail oldMission = coreMissionDetailService.getMissionDetailByMissionId(req.getId());
		if (oldMission == null) {
			throw new LogicException("任务不存在");
		}
		if (req.getStatusPropertyId() != oldMission.getStatusPropertyId()) {
			CoreMission cm = new CoreMission();
			cm.setId(req.getId());
			cm.setReason(req.getReason());
			cm.setStatusPropertyId(req.getStatusPropertyId());
			boolean ok = coreMissionService.updateById(cm);
			if (!ok) {
				log.error("MissionEditRequest = {}", req);
				throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMission失败");
			}
			if (req.getStatusPropertyId() == MissionPublishType.REVIEW_REJECT) {//如果是审核驳回，需要退还任务币和手续费，如果任务被封，所有都不退
				SysUser publishUser = sysUserService.getById(oldMission.getUserId());
				//任务币变动
				sysUserService.updateUserMissionCoin(publishUser, CurrencyChangeReasonType.PUBLISH_MISSION_REVIEW_REJECT, oldMission.getPrice() * oldMission.getCount() + oldMission.getFeePrice());
			}
			if (req.getStatusPropertyId() == MissionPublishType.BAN) {//任务被封，任务币和手续费不退，并将所有已经在做的任务变成被封状态，并填入finishtime
				coreMissionAcceptService.banMissionAccept(req.getId());
			}
			//发送通知
			String msg = null;
			if (req.getStatusPropertyId() == MissionPublishType.PUBLISHED) {
				msg = String.format("您的任务<%s>审核通过", oldMission.getTitle());
			}
			if (req.getStatusPropertyId() == MissionPublishType.BAN) {//被封
				msg = String.format("您的任务<%s>已被封禁", oldMission.getTitle());
				if (!Strings.isNullOrEmpty(req.getReason())) {
					msg += String.format(",原因：%s", req.getReason());
				}
			}
			if (req.getStatusPropertyId() == MissionPublishType.REVIEW_REJECT) {//审核驳回
				msg = String.format("您的任务<%s>审核不通过", oldMission.getTitle());
				if (!Strings.isNullOrEmpty(req.getReason())) {
					msg += String.format(",原因：%s", req.getReason());
				}
			}
			noticeService.sendNotice(NoticeType.RESULT_OF_PUBLISH_MISSION, msg, oldMission.getUserId());

		}
		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_delete') || isClientUser()")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreMission coreMission = getExistEntityById(idRequest.getId(), coreMissionService);
		if (coreMission.getStatusPropertyId() == MissionPublishType.PUBLISHED) {
			throw new LogicException("当前任务已发布，需先下架后再删除");
		}
		boolean ok = coreMissionService.removeById(coreMission);
		if (!ok) {
			log.error("coreMission = {}", coreMission);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreMission失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreMissionService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_mission') || isClientUser()")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreMissionDetailMapper.selectMissionDetailByMissionId(idRequest.getId());
	}

}
