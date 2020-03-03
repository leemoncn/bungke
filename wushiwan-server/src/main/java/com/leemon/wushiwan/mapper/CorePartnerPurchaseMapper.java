package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.dto.PartnerInfo;
import com.leemon.wushiwan.entity.CorePartnerPurchase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-06-27
 */
@Repository
public interface CorePartnerPurchaseMapper extends BaseMapper<CorePartnerPurchase> {

	PartnerInfo selectValidPartnerInfoByUser(@Param("userId") Integer userId);

	List<PartnerInfo> selectValidPartnerInfoList();
}
