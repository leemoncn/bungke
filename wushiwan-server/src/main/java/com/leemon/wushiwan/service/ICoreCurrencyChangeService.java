package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreCurrencyChange;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-06-16
 */
public interface ICoreCurrencyChangeService extends IService<CoreCurrencyChange> {

	/**
	 * 添加货币变动记录
	 *
	 * @param userId
	 * @param type
	 * @param change
	 */
	void addCurrencyChangeRecord(Integer userId, CurrencyType type, CurrencyChangeReasonType reasonType, Integer change);
}
