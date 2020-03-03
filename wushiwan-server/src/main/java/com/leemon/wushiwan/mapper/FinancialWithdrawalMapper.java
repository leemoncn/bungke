package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.entity.FinancialWithdrawal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-06-29
 */
@Component
public interface FinancialWithdrawalMapper extends BaseMapper<FinancialWithdrawal> {
	Integer selectUserInReviewMoney(@Param("userId") Integer userId);
}
