package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreBanner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-11-18
 */
public interface ICoreBannerService extends IService<CoreBanner> {

	/**
	 * 返回所有可用的banner
	 *
	 * @return
	 */
	List<CoreBanner> usableList();
}
