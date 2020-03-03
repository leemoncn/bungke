package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.entity.SocialReviewChat;
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
 * @since 2019-07-22
 */
@Component
public interface SocialReviewChatMapper extends BaseMapper<SocialReviewChat> {
	SocialReviewChat selectLastChat(@Param("missionAcceptId") Integer missionAcceptId);

	List<SocialReviewChat> selectAllChat(@Param("missionAcceptId") Integer missionAcceptId);
}
