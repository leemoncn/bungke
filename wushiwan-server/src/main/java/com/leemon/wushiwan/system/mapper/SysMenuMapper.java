package com.leemon.wushiwan.system.mapper;

import com.leemon.wushiwan.system.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leemon.wushiwan.system.vo.TreeModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {
	List<String> selectMenuPermissionsByMenuIdList(@Param("list") List<Integer> list);

	List<TreeModel> queryListByParentId(@Param("parentId") Integer parentId);

	/**
	 * 根据用户查询用户权限
	 */
	List<SysMenu> queryByUserId(@Param("userId") Integer userId);
}
