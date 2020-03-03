package com.leemon.wushiwan.controller;

import com.leemon.wushiwan.entity.CoreBanner;
import com.leemon.wushiwan.entity.CoreNotice;
import com.leemon.wushiwan.service.ICoreBannerService;
import com.leemon.wushiwan.service.ICoreNoticeService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 首页
 * @author: limeng
 * @create: 2019-11-21 21:54
 **/
@RestController
@RequestMapping("/home")
@Slf4j
public class HomeController extends BaseController {

	private final ICoreBannerService bannerService;
	private final ICoreNoticeService noticeService;

	public HomeController(ICoreBannerService bannerService, ICoreNoticeService noticeService) {
		this.bannerService = bannerService;
		this.noticeService = noticeService;
	}

	@Data
	private static class HomeDataResponse {
		private List<CoreBanner> bannerList;
		private List<CoreNotice> noticeList;
	}

	@PostMapping("/data")
	public Object homeData() {
		HomeDataResponse res = new HomeDataResponse();
		res.setBannerList(bannerService.usableList());
		res.setNoticeList(noticeService.usableList());
		return res;
	}
}
