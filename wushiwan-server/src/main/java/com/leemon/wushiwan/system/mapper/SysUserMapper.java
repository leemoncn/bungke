package com.leemon.wushiwan.system.mapper;

import com.leemon.wushiwan.system.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.vo.SysUserDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Mapper 其他类注入的时候只能注入SysUserService，禁止注入SysUserMapper。SysUserMapper只能注入到SysUserService里面
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Component
public interface SysUserMapper extends BaseMapper<SysUser> {

	SysUserDetail selectSysUserDetailById(@Param("userId") Integer userId);
}
