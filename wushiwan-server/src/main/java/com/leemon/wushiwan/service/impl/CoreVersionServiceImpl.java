package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.entity.CoreVersion;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreVersionMapper;
import com.leemon.wushiwan.service.ICoreVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.JenkinsTriggerHelper;
import com.offbytwo.jenkins.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-08-18
 */
@Slf4j
@Service
public class CoreVersionServiceImpl extends ServiceImpl<CoreVersionMapper, CoreVersion> implements ICoreVersionService {

	private final Config config;

	public CoreVersionServiceImpl(Config config) {
		this.config = config;
	}

	@Override
	public List<CoreVersion> getVersionListGreaterThanVersion(String versionName, Integer versionCode) {
		QueryWrapper<CoreVersion> qw = new QueryWrapper<>();
		qw.lambda().ge(CoreVersion::getVersionName, versionName)
				.or()
				.ge(CoreVersion::getVersionCode, versionCode);
		return this.list(qw);
	}

	/**
	 * 开始一个jenkins任务
	 *
	 * @param jobName
	 */
	@Override
	public boolean startJenkinsJob(String jobName) {
		log.info("开始执行jenkins任务{}", jobName);
		JenkinsServer jenkins;
		try {
			jenkins = new JenkinsServer(new URI(config.getJenkinsServerUrl()), config.getJenkinsAccount(), config.getJenkinsPassword());
		} catch (URISyntaxException e) {
			throw new LogicException(ErrorCode.SYS_ERROR, e.getMessage());
		}
		Map<String, Job> jobs;
		try {
			jobs = jenkins.getJobs();
		} catch (IOException e) {
			throw new LogicException(ErrorCode.SYS_ERROR, e.getMessage());
		}
		if (jobs == null || !jobs.containsKey(jobName)) {
			throw new LogicException("jenkins任务不存在，请联系技术支持");
		}

		JenkinsTriggerHelper jth = new JenkinsTriggerHelper(jenkins);
		BuildWithDetails result;
		try {
			result = jth.triggerJobAndWaitUntilFinished(jobName);
		} catch (Exception e) {
			throw new LogicException(ErrorCode.SYS_ERROR, e.getMessage());
		}
		return result.getResult().equals(BuildResult.SUCCESS);
		// go further
//		job.details().getBuildByNumber(1).details()
//		JobWithDetails job = jobs.get("My Job").details()
	}

}
