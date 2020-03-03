package com.leemon.wushiwan.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.entity.SocialNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.vo.NoticeVO;
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
public interface SocialNoticeMapper extends BaseMapper<SocialNotice> {
	IPage<NoticeVO> selectNoticeList(Page page, @Param("userId") Integer userId);
}
