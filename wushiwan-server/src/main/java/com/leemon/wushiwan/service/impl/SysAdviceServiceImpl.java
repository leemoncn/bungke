package com.leemon.wushiwan.service.impl;

import com.google.common.base.Strings;
import com.leemon.wushiwan.entity.SysAdvice;
import com.leemon.wushiwan.enums.CoreImgTypeMission;
import com.leemon.wushiwan.mapper.SysAdviceMapper;
import com.leemon.wushiwan.service.ICoreImgService;
import com.leemon.wushiwan.service.ISysAdviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.service.QiniuKodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
@Slf4j
public class SysAdviceServiceImpl extends ServiceImpl<SysAdviceMapper, SysAdvice> implements ISysAdviceService {

	private final ICoreImgService imgService;
	private final QiniuKodoService kodoService;

	public SysAdviceServiceImpl(ICoreImgService imgService, QiniuKodoService kodoService) {
		this.imgService = imgService;
		this.kodoService = kodoService;
	}

	/**
	 * 添加新的建议
	 *
	 * @param message
	 * @param contactInfo
	 * @param userId
	 * @param redisKey
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public SysAdvice addNewAdvice(String message, String contactInfo, Integer userId, String redisKey) {
		List<String> pathList = kodoService.deleteAllFileUploadedRedisKey(redisKey);
		SysAdvice advice = new SysAdvice();
		advice.setUserId(userId);
		advice.setContactInfo(contactInfo);
		advice.setMessage(message);
		advice.setHaveImg(!Strings.isNullOrEmpty(redisKey));
		save(advice);
		imgService.saveImgListToDB(userId, advice.getId(), CoreImgTypeMission.ADVICE, pathList);
		return advice;
	}
}
