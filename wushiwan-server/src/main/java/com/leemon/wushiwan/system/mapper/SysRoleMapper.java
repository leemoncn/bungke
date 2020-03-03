package com.leemon.wushiwan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.system.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {
	List<SysRole> selectRolesByUserId(@Param("userId") Integer userId);
}
