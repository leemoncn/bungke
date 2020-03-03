package com.leemon.wushiwan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.entity.BaseEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.NoticeType;
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
public class SocialNotice extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("user_id")
	private Integer userId;

	/**
	 * 通知类型
	 */
	@TableField("type_property_id")
	private NoticeType typePropertyId;

	@TableField("msg1")
	private String msg1;

	@TableField("msg2")
	private String msg2;

	@TableField("msg3")
	private String msg3;

	@TableField("msg4")
	private String msg4;

	@TableField("msg5")
	private String msg5;

}
