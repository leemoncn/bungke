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
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreNotice extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 文字内容
	 */
	@TableField("text")
	private String text;

	/**
	 * 点击后跳转连接，可以是html代码，也可以是网址
	 */
	@TableField("html")
	private String html;

	/**
	 * 排序
	 */
	@TableField("sort")
	private Integer sort;

	/**
	 * 是否可用
	 */
	@TableField("usable")
	private Boolean usable;


}
