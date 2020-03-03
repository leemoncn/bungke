package com.leemon.wushiwan.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.google.common.base.Strings;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.CoreImg;
import com.leemon.wushiwan.enums.CoreImgTypeMission;
import com.leemon.wushiwan.enums.NoticeType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.SocialReviewChat;
import com.leemon.wushiwan.service.ICoreImgService;
import com.leemon.wushiwan.service.ICoreMissionDetailService;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.service.ISocialReviewChatService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 审核聊天记录
 * @author: leemon
 * @date: 2019-07-22
 */
@RestController
@RequestMapping("/social-review-chat")
@Slf4j
public class SocialReviewChatController extends BaseController<SocialReviewChat> {

	private final ISocialReviewChatService socialReviewChatService;
	private final ICoreMissionDetailService detailService;
	private final ISocialNoticeService noticeService;
	private final ICoreImgService coreImgService;
	private final ISysUserService sysUserService;

	@Autowired
	public SocialReviewChatController(ISocialReviewChatService socialReviewChatService, ICoreMissionDetailService detailService, ISocialNoticeService noticeService, ICoreImgService coreImgService, ISysUserService sysUserService) {
		this.socialReviewChatService = socialReviewChatService;
		this.detailService = detailService;
		this.noticeService = noticeService;
		this.coreImgService = coreImgService;
		this.sysUserService = sysUserService;
	}

	@Data
	private static class AddChatRequest {
		@NotBlank
		@Length(max = 255)
		private String text;
		String key;
		@NotNull
		private Integer missionId;
		@NotNull
		private Integer missionAcceptId;
		@NotNull
		private Integer toUserId;
	}

	/**
	 * 通过回复按钮提交的，没有状态
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping("/add-chat")
	public Object addChat(@Valid @RequestBody AddChatRequest req) {
		MissionDetail md = detailService.getMissionDetailByMissionId(req.getMissionId());
		if (md == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "任务不存在");
		}
		if (md.getUserId().equals(UserUtil.getSysUserId()) && req.getToUserId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "商家给用户发送消息必须填toUserId");
		}
		socialReviewChatService.addNewChat(req.getText(), md, req.getKey(), UserUtil.getSysUserId(), req.getToUserId(), req.getMissionAcceptId(), null);
		//添加消息
		String msg = Strings.lenientFormat("任务<%s>有新的回复，请立即查看", md.getTitle());
		noticeService.sendNotice(NoticeType.RECEIVE_CHAT, msg, req.getToUserId());
		return null;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	private static class ChatListResponse extends SocialReviewChat {
		private List<CoreImg> imgList;
		private String chatTime;
		private String fromUserHeadImg;
		private String fromUserName;
	}

	@PreAuthorize("hasPermission('social_review_chat_edit')")
	@PostMapping("/chat-list")
	public Object chatList(@Valid @RequestBody IdRequest req) {
		//和商家的聊天记录
		List<SocialReviewChat> socialReviewChatList = socialReviewChatService.getAllChat(req.getId());
		Collections.reverse(socialReviewChatList);
		List<ChatListResponse> chatList = new ArrayList<>();
		socialReviewChatList.forEach(socialReviewChat -> {
			ChatListResponse r = new ChatListResponse();
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
		return chatList;
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

	/**
	 * 分页列表查询
	 *
	 * @param socialReviewChat
	 * @return
	 */
	@PreAuthorize("hasPermission('social_review_chat')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid SocialReviewChat socialReviewChat) {
		Page<SocialReviewChat> page = new Page<>(socialReviewChat.getPageNo(), socialReviewChat.getPageSize());
		return socialReviewChatService.page(page, getLikeQueryWrapper(socialReviewChat));
	}

	/**
	 * 保存
	 *
	 * @param socialReviewChat
	 * @return
	 */
	@PreAuthorize("hasPermission('social_review_chat_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid SocialReviewChat socialReviewChat) {
		if (socialReviewChat.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		socialReviewChatService.saveOrUpdate(socialReviewChat);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param socialReviewChat
	 * @return
	 */
	@PreAuthorize("hasPermission('social_review_chat_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid SocialReviewChat socialReviewChat) {
		if (socialReviewChat.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = socialReviewChatService.updateById(socialReviewChat);
		if (!ok) {
			log.error("socialReviewChat = {}", socialReviewChat);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SocialReviewChat失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_review_chat_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		SocialReviewChat socialReviewChat = getExistEntityById(idRequest.getId(), socialReviewChatService);
		boolean ok = socialReviewChatService.removeById(socialReviewChat);
		if (!ok) {
			log.error("socialReviewChat = {}", socialReviewChat);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SocialReviewChat失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_review_chat_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		socialReviewChatService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('social_review_chat')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return socialReviewChatService.getById(idRequest.getId());
	}

}
