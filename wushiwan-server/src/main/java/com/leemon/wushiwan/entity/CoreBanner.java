package com.leemon.wushiwan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

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
public class CoreBanner extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * banner图片地址
	 */
	@TableField("img")
	private String img;

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

	/**
	 * 上传图片所保存的redis的key
	 */
	@TableField(exist = false)
	@JSONField(serialize = false)
	private String key;
}
