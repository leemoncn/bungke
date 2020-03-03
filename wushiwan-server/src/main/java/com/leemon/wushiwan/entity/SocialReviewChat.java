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
 * @since 2019-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SocialReviewChat extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务ID
	 */
	@TableField("mission_id")
	private Integer missionId;

	/**
	 * 接取任务表的id
	 */
	@TableField("mission_accept_id")
	private Integer missionAcceptId;

	/**
	 * 信息发出者
	 */
	@TableField("from_user_id")
	private Integer fromUserId;

	/**
	 * 信息接受者
	 */
	@TableField("to_user_id")
	private Integer toUserId;

	/**
	 * 是否包含图片
	 */
	@TableField("include_img")
	private Boolean includeImg;

	/**
	 * 文字
	 */
	@TableField("text")
	private String text;

	/**
	 * 状态文字
	 */
	@TableField("status")
	private String status;

	/**
	 * 上一条对话ID，如果是第一条为null
	 */
	@TableField("previous_chat_id")
	private Integer previousChatId;


}
