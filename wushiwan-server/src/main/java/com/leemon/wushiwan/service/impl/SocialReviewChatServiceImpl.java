package com.leemon.wushiwan.service.impl;

import com.google.common.base.Strings;
import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.SocialReviewChat;
import com.leemon.wushiwan.enums.CoreImgTypeMission;
import com.leemon.wushiwan.mapper.SocialReviewChatMapper;
import com.leemon.wushiwan.service.ICoreImgService;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.service.ISocialReviewChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.service.QiniuKodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-07-22
 */
@Service
public class SocialReviewChatServiceImpl extends ServiceImpl<SocialReviewChatMapper, SocialReviewChat> implements ISocialReviewChatService {

	private final SocialReviewChatMapper chatMapper;
	private final ICoreImgService imgService;
	private final QiniuKodoService kodoService;
	private final ISocialNoticeService noticeService;

	public SocialReviewChatServiceImpl(SocialReviewChatMapper chatMapper, ICoreImgService imgService, QiniuKodoService kodoService, ISocialNoticeService noticeService) {
		this.chatMapper = chatMapper;
		this.imgService = imgService;
		this.kodoService = kodoService;
		this.noticeService = noticeService;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addNewChat(String text, MissionDetail missionDetail, String redisKey, Integer fromUserId, Integer toUserId, Integer missionAcceptId, String status) {
		SocialReviewChat src = new SocialReviewChat();
		src.setMissionId(missionDetail.getMissionId());
		src.setMissionAcceptId(missionAcceptId);
		src.setText(text);
		src.setStatus(status);
		src.setFromUserId(fromUserId);
		src.setToUserId(toUserId);
		src.setIncludeImg(!Strings.isNullOrEmpty(redisKey));
		//查找之前有没有对话记录，如果有，取最后一条
		SocialReviewChat lastChat = chatMapper.selectLastChat(missionAcceptId);
		if (lastChat != null) {
			src.setPreviousChatId(lastChat.getId());
		}
		save(src);
		//保存图片
		List<String> imgPathList = kodoService.deleteAllFileUploadedRedisKey(redisKey);
		imgService.saveImgListToDB(fromUserId, src.getId(), CoreImgTypeMission.REVIEW_CHAT, imgPathList);
	}

	@Override
	public List<SocialReviewChat> getAllChat(Integer missionAcceptId) {
		return chatMapper.selectAllChat(missionAcceptId);
	}
}
