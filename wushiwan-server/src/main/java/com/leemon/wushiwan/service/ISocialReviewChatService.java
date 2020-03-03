package com.leemon.wushiwan.service;

import com.leemon.wushiwan.dto.MissionDetail;
import com.leemon.wushiwan.entity.SocialReviewChat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-07-22
 */
public interface ISocialReviewChatService extends IService<SocialReviewChat> {

	void addNewChat(String text, MissionDetail missionDetail, String redisKey, Integer fromUserId, Integer toUserId, Integer missionAcceptId, String status);

	List<SocialReviewChat> getAllChat(Integer missionAcceptId);
}
