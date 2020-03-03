package com.leemon.wushiwan.system.entity;

import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户-角色
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysUserRole extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@TableField("user_id")
	private Integer userId;

	/**
	 * 角色ID
	 */
	@TableField("role_id")
	private Integer roleId;


}
