package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CorePartner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ICorePartnerService extends IService<CorePartner> {
	/**
	 * 获取非合作商
	 *
	 * @return
	 */
	CorePartner getNonePartner();
}
