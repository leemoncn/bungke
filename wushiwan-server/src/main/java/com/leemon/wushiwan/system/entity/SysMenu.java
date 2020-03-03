package com.leemon.wushiwan.system.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.SysMenuType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author leemon
 * @since 2019-04-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysMenu extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 父级编号
	 */
	@TableField(value = "parent_id")
	private Integer parentId;

	/**
	 * 菜单标题
	 */
	@TableField("name")
	private String name;

	/**
	 * 排序
	 */
	@TableField("sort")
	private BigDecimal sort;

	/**
	 * 链接
	 */
	@TableField("href")
	private String href;

	/**
	 * 图标
	 */
	@TableField("icon")
	private String icon;

	/**
	 * 是否在菜单中显示
	 */
	@TableField("is_show")
	private Boolean isShow;

	/**
	 * 组件的路径
	 */
	@TableField("component")
	private String component;

	/**
	 * 组件的名字
	 */
	@TableField("component_name")
	private String componentName;

	/**
	 * 权限标识
	 */
	@TableField("permission")
	private String permission;


}
