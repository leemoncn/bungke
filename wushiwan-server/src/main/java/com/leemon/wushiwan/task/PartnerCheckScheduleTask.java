package com.leemon.wushiwan.task;

import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.dto.PartnerInfo;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.enums.NoticeType;
import com.leemon.wushiwan.service.ICorePartnerPurchaseService;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.service.ISocialNoticeService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * @description: 每分钟检查用户接取的任务是否超过了截止日期，以及做任务时间是否超时
 * @author: leemon
 * @create: 2019-05-28 16:20
 **/
@Component
@Slf4j
public class PartnerCheckScheduleTask {

	private final ICorePartnerPurchaseService partnerPurchaseService;
	private final ISocialNoticeService noticeService;
	private final ISysUserService sysUserService;
	private final ICorePartnerService corePartnerService;
	private final Config config;

	@Autowired
	public PartnerCheckScheduleTask(ICorePartnerPurchaseService partnerPurchaseService, ISocialNoticeService noticeService, ISysUserService sysUserService, ICorePartnerService corePartnerService, Config config) {
		this.partnerPurchaseService = partnerPurchaseService;
		this.noticeService = noticeService;
		this.sysUserService = sysUserService;
		this.corePartnerService = corePartnerService;
		this.config = config;
	}


	@Scheduled(cron = "0 30 0 * * ?")//每天凌晨0点半执行
	private void configureTasks() {
		List<PartnerInfo> list = partnerPurchaseService.getValidPartnerInfoList();
		LocalDate now = LocalDate.now();
		list.forEach(cpp -> {
			LocalDate endTime = cpp.getEndTime();
			if (now.isEqual(endTime) || now.isAfter(endTime)) {//合作商计划到期
				partnerPurchaseService.disablePartner(cpp.getId());
				CorePartner cp = corePartnerService.getNonePartner();
				SysUser sysUser = sysUserService.getById(cpp.getUserId());
				sysUserService.updateUserPartnerId(sysUser, cp.getId());
				String msg = String.format("您的合作商计划<%s>已过期，请及时充值。", cpp.getName());
				//发送到期通知
				noticeService.sendNotice(NoticeType.PARTNER_EXPIRED, msg, cpp.getUserId());
			}
		});
	}
}
