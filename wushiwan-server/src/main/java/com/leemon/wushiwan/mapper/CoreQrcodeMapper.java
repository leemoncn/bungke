package com.leemon.wushiwan.mapper;

import com.leemon.wushiwan.entity.CoreQrcode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.system.entity.SysUser;
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
public interface CoreQrcodeMapper extends BaseMapper<CoreQrcode> {

	SysUser selectUserByUUID(@Param("uuid") String uuid);
}
