package com.leemon.wushiwan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.dto.ShopDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Repository
public interface ShopMapper extends BaseMapper<ShopDetail> {

	ShopDetail selectShopDetail(@Param("userId") Integer userId);
}
