package com.leemon.wushiwan.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.entity.CoreQrcode;
import com.leemon.wushiwan.service.ICoreQrcodeService;
import com.leemon.wushiwan.util.QRCodeUtil;
import com.leemon.wushiwan.util.UserUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Slf4j
@RestController
@RequestMapping("/core-qrcode")
public class CoreQrcodeController extends BaseController<CoreQrcode> {


	private final ICoreQrcodeService coreQrcodeService;
	private final Config config;

	@Autowired
	public CoreQrcodeController(ICoreQrcodeService coreQrcodeService, Config config) {
		this.coreQrcodeService = coreQrcodeService;
		this.config = config;
	}

	@Data
	private static class QRCodeRequest {
		@NotNull
		@Min(100)
		@Max(1000)
		private Integer width;
		@NotNull
		@Min(100)
		@Max(1000)
		private Integer height;
	}

	@Data
	private static class QRCodeResponse {
		private String base64Img;
		private String url;
	}

	@RequestMapping("/img")
	public Object qrcode(@RequestBody @Valid QRCodeRequest req) {
		Integer userId = UserUtil.getSysUserId();
		QueryWrapper<CoreQrcode> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreQrcode::getUserId, userId);
		CoreQrcode cq = coreQrcodeService.getOne(qw);
		if (cq == null) {//生成二维码
			cq = coreQrcodeService.addNewCoreQrcode(userId);
		}
		LocalDateTime expireTime = cq.getExpireTime();
		if (expireTime.isBefore(LocalDateTime.now())) {
			//TODO 添加二维码过期逻辑，用户可花钱购买永久二维码
			log.error("用户[{}]二维码已过期", userId);
		}
		String randomHost = config.getClientDomain().replace("#", "?t=" + System.currentTimeMillis() + "#");
		String url = String.format("%s?uuid=%s", randomHost + "/mobile-login", cq.getUuid());
		QRCodeResponse res = new QRCodeResponse();
		res.setBase64Img(QRCodeUtil.convertToHtmlImgFormat(QRCodeUtil.strToQRCodeBase64Str(url, req.getWidth(), req.getHeight())));
		res.setUrl(url);
		return res;
	}
}
