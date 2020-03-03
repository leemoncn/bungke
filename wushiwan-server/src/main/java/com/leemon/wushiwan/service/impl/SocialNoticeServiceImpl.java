package com.leemon.wushiwan.service.impl;

import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.dto.PushModel;
import com.leemon.wushiwan.dto.PushPayloadNotice;
import com.leemon.wushiwan.entity.SocialNotice;
import com.leemon.wushiwan.enums.NoticeType;
import com.leemon.wushiwan.mapper.SocialNoticeMapper;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.service.PushService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
public class SocialNoticeServiceImpl extends ServiceImpl<SocialNoticeMapper, SocialNotice> implements ISocialNoticeService {

	private final PushService pushService;
	private final Config config;
	@Autowired
	private ISysUserService userService;

	public SocialNoticeServiceImpl(PushService pushService, Config config) {
		this.pushService = pushService;
		this.config = config;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public String sendNotice(NoticeType noticeType, String msg, Integer toUserId) {
		String title;
		switch (noticeType) {
			case RESULT_OF_PUBLISH_MISSION:
				title = "任务审核";
				break;
			case RESULT_OF_FINISH_MISSION:
				title = "任务审核";
				break;
			case PARTNER_EXPIRED:
				title = "合作商过期";
				break;
			case WITHDRAWAL_REVIEW_FINISH:
				title = "提现审核";
				break;
			case WITHDRAWAL_REQUEST:
				title = "提现申请";
				break;
			case TOP_MISSION:
				title = "任务置顶";
				break;
			case MISSION_OVER_DEADLINE:
				title = "任务超时";
				break;
			case RECEIVE_CHAT:
				title = "收到新消息";
				break;
			case COMMIT_MISSION:
				title = "提交任务";
				break;
			default:
				title = config.getAlipayTransferName();
		}
		return sendNotice(noticeType, msg, toUserId, title);
	}

	@Override
	public String sendNotice(NoticeType noticeType, String msg, Integer toUserId, String notificationTitle) {
		SocialNotice sn = new SocialNotice();
		sn.setMsg1(msg);
		sn.setTypePropertyId(noticeType);
		sn.setUserId(toUserId);
		this.save(sn);

		if (noticeType == NoticeType.FROM_ADMIN) {//后台推送
			PushModel model = getNotice(notificationTitle, msg);
			String pushId = null;
			if (toUserId != null) {
				SysUser toUser = userService.getById(toUserId);
				pushId = toUser.getPushId();
			}
			return pushService.pushMsg(model, pushId);
		} else if (noticeType != NoticeType.WITHDRAWAL_REQUEST && noticeType != NoticeType.TOP_MISSION && noticeType != NoticeType.COMMIT_MISSION) {//这几种类型不需要发推送
			String pushId = null;
			if (toUserId != null) {
				SysUser toUser = userService.getById(toUserId);
				pushId = toUser.getPushId();
			}
			if (Strings.isNullOrEmpty(pushId)) {//这几种类型是系统自动通知，都是单条推送，如果没有pushid，不能向全体发
				return null;
			}
			PushModel model = getNotice(notificationTitle, msg);
			return pushService.pushMsg(model, pushId);
		}
		return null;
	}

	private PushModel getNotice(String title, String msg) {
		PushPayloadNotice notice = new PushPayloadNotice();
		notice.setMsg(msg);
		PushModel model = PushModel.builder().title(title).content(msg).build();
		model.addSinglePayload(notice);
		return model;
	}
}
