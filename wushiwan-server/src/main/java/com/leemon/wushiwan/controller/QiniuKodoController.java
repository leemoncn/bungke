package com.leemon.wushiwan.controller;

import com.leemon.wushiwan.service.QiniuKodoService;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IntRequest;
import com.leemon.wushiwan.vo.UploadTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * @description: 七牛云上传图片
 * @author: limeng
 * @create: 2019-11-18 21:56
 **/
@RestController
@RequestMapping("/qiniu/kodo")
@Slf4j
public class QiniuKodoController {
	private final QiniuKodoService kodoService;

	public QiniuKodoController(QiniuKodoService kodoService) {
		this.kodoService = kodoService;
	}

	@PostMapping("/create-token")
	public Object detail(@Valid @RequestBody IntRequest req) {
		Integer userId = UserUtil.getSysUserId();
		Integer number = req.getValue();
		return kodoService.createUploadFileToken(userId, number);
	}

	@PostMapping("/upload-success")
	public Object uploadSuccess(@RequestBody Map<String, String> map) {
		try {
			kodoService.fileUploadSuccess(map.get("redisKey"), map.get("key"));
		} catch (Exception e) {
			log.error("接受七牛云回调错误 = {}", e.getMessage());
		}
		return map;
	}
}
