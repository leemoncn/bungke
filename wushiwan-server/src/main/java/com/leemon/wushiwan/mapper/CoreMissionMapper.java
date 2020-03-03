package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.entity.CoreMission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface CoreMissionMapper extends BaseMapper<CoreMission> {

	void updateAllMissionOverTopEnd();
}
