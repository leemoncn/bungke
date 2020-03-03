package com.leemon.wushiwan.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.entity.CoreVersion;
import com.leemon.wushiwan.service.ICoreImgService;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.service.ICoreVersionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @Description: 抽成配置
 * @author: leemon
 * @date: 2019-06-29
 */
@RestController
@RequestMapping("/index")
@Slf4j
public class IndexController {

	private final CacheManager cacheManager;
	private final ICorePartnerService partnerService;
	private final ICoreImgService imgService;
	private final ICoreVersionService versionService;

	@Autowired
	public IndexController(CacheManager cacheManager, ICorePartnerService partnerService, ICoreImgService imgService, ICoreVersionService versionService) {
		this.cacheManager = cacheManager;
		this.partnerService = partnerService;
		this.imgService = imgService;
		this.versionService = versionService;
	}

	/**
	 * 清空redis缓存
	 *
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/clean-cache")
	public Object cleanCache() {
		cacheManager.getCacheNames().parallelStream().forEach(name -> {//这里只会清理springcache管理的redis的key，通过redistemplate添加的不会清理，比如jwt black list
			Objects.requireNonNull(cacheManager.getCache(name)).clear();
			log.info("清理内容 = {}", name);
		});
		return null;
	}

	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/test")
	public Object test() {
		partnerService.getNonePartner();
		return null;
	}

	@Data
	public static class AddVersionRequest {
		/**
		 * 版本号
		 */
		private Integer versionCode;

		/**
		 * 版本名
		 */
		private String versionName;

		/**
		 * wgt文件
		 */
		private String wgtFilename;

		/**
		 * 强制更新
		 */
		private Boolean forceUpdate;
	}

	/**
	 * 在wushiwan服务器上打包，并拷贝到正式服务器上之后，通过请求远程服务器的这个接口，新版本数据
	 */
	@RequestMapping("/no-auth/add-version")
	public Object saveImgToDisk(@RequestBody @Valid AddVersionRequest req) {
		CoreVersion cv = new CoreVersion();
		cv.setDeployed(true);
		cv.setVersionCode(req.getVersionCode());
		cv.setVersionName(req.getVersionName());
		cv.setWgtFilename(req.getWgtFilename());
		cv.setForceUpdate(req.getForceUpdate());
		versionService.save(cv);
		return "success";
	}
}
