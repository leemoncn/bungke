package com.leemon.wushiwan.entity;

import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysProperty extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 属性类型名称，如果出现新的type值,需要再代码里面PropertyType里面添加
	 */
	@TableField("type")
	private Integer type;

	/**
	 * 属性名字
	 */
	@TableField("name")
	private String name;

	/**
	 * 属性值
	 */
	@TableField("value")
	private String value;


}
