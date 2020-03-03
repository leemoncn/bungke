package com.leemon.wushiwan.system.entity;

import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysRole extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色名称(一般是中文)
	 */
	@TableField("name")
	private String name;

	/**
	 * 角色英文名称
	 */
	@TableField("role_name")
	private String roleName;

	/**
	 * 是否可用
	 */
	@TableField("useable")
	private Boolean useable;


}
