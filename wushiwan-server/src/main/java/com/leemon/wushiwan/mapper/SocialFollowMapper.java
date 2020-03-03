package com.leemon.wushiwan.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.dto.FansDetail;
import com.leemon.wushiwan.entity.SocialFollow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Component
public interface SocialFollowMapper extends BaseMapper<SocialFollow> {

	/**
	 * 用户已关注列表
	 *
	 * @param userId
	 * @return
	 */
	IPage<FansDetail> selectFollowedUserList(Page page, @Param("userId") Integer userId);

	/**
	 * 用户粉丝列表
	 *
	 * @param userId
	 * @return
	 */
	IPage<FansDetail> selectFansUserList(Page page, @Param("userId") Integer userId);

	Boolean selectIfFollowedUser(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
}
