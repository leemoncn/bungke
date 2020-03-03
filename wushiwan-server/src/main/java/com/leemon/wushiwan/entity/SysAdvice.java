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
public class SysAdvice extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("user_id")
	private Integer userId;

	/**
	 * 意见反馈内容
	 */
	@TableField("message")
	private String message;

	/**
	 * 是否包含图片
	 */
	@TableField("have_img")
	private Boolean haveImg;

	/**
	 * 联系方式
	 */
	@TableField("contact_info")
	private String contactInfo;
}
