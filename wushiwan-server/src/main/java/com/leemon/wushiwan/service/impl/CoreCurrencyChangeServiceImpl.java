package com.leemon.wushiwan.service.impl;

import com.leemon.wushiwan.entity.CoreCurrencyChange;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreCurrencyChangeMapper;
import com.leemon.wushiwan.service.ICoreCurrencyChangeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-06-16
 */
@Service
public class CoreCurrencyChangeServiceImpl extends ServiceImpl<CoreCurrencyChangeMapper, CoreCurrencyChange> implements ICoreCurrencyChangeService {

	/**
	 * 添加货币变动记录
	 *
	 * @param userId
	 * @param type
	 * @param change
	 */
	@Override
	public void addCurrencyChangeRecord(Integer userId, CurrencyType type, CurrencyChangeReasonType reasonType, Integer change) {
		CoreCurrencyChange ccc = new CoreCurrencyChange();
		ccc.setUserId(userId);
		ccc.setTypePropertyId(type);
		ccc.setReasonPropertyId(reasonType);
		ccc.setRecord(change);
		if (!this.saveOrUpdate(ccc)) {
			throw new LogicException(ErrorCode.SYS_ERROR, "添加货币变动记录失败");
		}
	}
}
