package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.entity.CoreMissionDetailStep;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.dto.ExplainData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-08-15
 */
@Component
public interface CoreMissionDetailStepMapper extends BaseMapper<CoreMissionDetailStep> {

	List<ExplainData> selectExplainData(@Param("userId") Integer userId, @Param("type") Integer type, @Param("missionDetailId") Integer missionDetailId);
}
