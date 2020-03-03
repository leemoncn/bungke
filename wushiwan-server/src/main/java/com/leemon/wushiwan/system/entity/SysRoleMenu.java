package com.leemon.wushiwan.system.entity;

import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色-菜单
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysRoleMenu extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
	@TableField("role_id")
	private Integer roleId;

	/**
	 * 菜单ID
	 */
	@TableField("menu_id")
	private Integer menuId;

	/**
	 * 添加权限
	 */
	@TableField("add_permission")
	private String addPermission;

	/**
	 * 编辑权限
	 */
	@TableField("edit_permission")
	private String editPermission;

	/**
	 * 删除权限
	 */
	@TableField("delete_permission")
	private String deletePermission;
}
