package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreVersion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-08-18
 */
public interface ICoreVersionService extends IService<CoreVersion> {

	/**
	 * 版本号或版本名大于传入的
	 *
	 * @param versionName
	 * @param versionCode
	 * @return
	 */
	List<CoreVersion> getVersionListGreaterThanVersion(String versionName, Integer versionCode);

	/**
	 * 开始一个jenkins任务
	 *
	 * @param jobName
	 */
	boolean startJenkinsJob(String jobName);
}
