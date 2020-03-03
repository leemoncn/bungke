package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.entity.CoreImg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * Mapper 禁止直接注入CoreImgMapper，CoreImgMapper只允许注入到CoreImgService里面
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface CoreImgMapper extends BaseMapper<CoreImg> {

}
