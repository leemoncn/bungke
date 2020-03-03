package com.leemon.wushiwan.controller;

import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.dto.PushModel;
import com.leemon.wushiwan.dto.PushPayloadNotice;
import com.leemon.wushiwan.dto.PushPayloadToast;
import com.leemon.wushiwan.enums.NoticeType;
import com.leemon.wushiwan.enums.PushPayloadType;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.service.PushService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 推送
 * @author: limeng
 * @create: 2019-11-18 21:56
 **/
@RestController
@RequestMapping("/push")
@Slf4j
public class PushController {

	private final Config config;
	private final PushService pushService;
	private final ISocialNoticeService socialNoticeService;

	public PushController(Config config, PushService pushService, ISocialNoticeService socialNoticeService) {

		this.config = config;
		this.pushService = pushService;
		this.socialNoticeService = socialNoticeService;
	}

	@Data
	private static class PushRequest {
		@NotBlank
		private String title;
		@NotBlank
		private String content;
		@NotNull
		private PushPayloadType payloadType;
		//如果是空，就向全体成员发
		private Integer userId;
	}

	@PostMapping("/create")
	public Object uploadSuccess(@RequestBody @Valid PushRequest req) {
		if (req.getPayloadType() == PushPayloadType.NOTICE) {
			return socialNoticeService.sendNotice(NoticeType.FROM_ADMIN, req.getContent(), req.getUserId(), req.getTitle());
		}
		return null;
	}
}
