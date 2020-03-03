package com.leemon.wushiwan.system.service;

import com.leemon.wushiwan.system.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.system.vo.TreeModel;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
public interface ISysMenuService extends IService<SysMenu> {
	List<TreeModel> queryListByParentId(Integer parentId);

	void deletePermissionLogical(Integer id);

	void addSysMenu(SysMenu sysMenu);

	void editSysMenu(SysMenu sysMenu);

	List<SysMenu> queryByUserId(Integer userId);
}
