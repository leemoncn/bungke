package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.entity.CoreTop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-07-25
 */
@Component
public interface CoreTopMapper extends BaseMapper<CoreTop> {

	/**
	 * 查出所有今天创建的指定记录
	 *
	 * @param userId
	 * @return
	 */
	List<CoreTop> selectTodayTopRecord(@Param("userId") Integer userId);

	/**
	 * 查出最近的一条置顶记录
	 *
	 * @param userId
	 * @param missionId
	 * @return
	 */
	CoreTop selectLastCoreTop(@Param("userId") Integer userId, @Param("missionId") Integer missionId);
}
