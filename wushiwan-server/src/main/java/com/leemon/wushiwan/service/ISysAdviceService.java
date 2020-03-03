package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.SysAdvice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ISysAdviceService extends IService<SysAdvice> {
	/**
	 * 添加新的建议
	 *
	 * @param message
	 * @param contactInfo
	 * @param userId
	 * @param redisKey
	 */
	SysAdvice addNewAdvice(String message, String contactInfo, Integer userId, String redisKey);
}
