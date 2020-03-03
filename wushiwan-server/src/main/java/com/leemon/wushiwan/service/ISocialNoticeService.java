package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.SocialNotice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.enums.NoticeType;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ISocialNoticeService extends IService<SocialNotice> {
	String sendNotice(NoticeType noticeType, String msg, Integer toUserId);

	String sendNotice(NoticeType noticeType, String msg, Integer toUserId, String notificationTitle);
}
