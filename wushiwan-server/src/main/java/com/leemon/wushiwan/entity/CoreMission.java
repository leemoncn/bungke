package com.leemon.wushiwan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.entity.BaseEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.MissionPublishType;
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
public class CoreMission extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("mission_detail_id")
	private Integer missionDetailId;

	/**
	 * 任务发布者的id
	 */
	@TableField("user_id")
	private Integer userId;

	/**
	 * 任务发布时间
	 */
	@TableField("publish_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime publishTime;

	/**
	 * 任务当前状态（被封、数量完成、到期等各种情况）
	 */
	@TableField("status_property_id")
	private MissionPublishType statusPropertyId;

	/**
	 * 任务置顶结束时间
	 */
	@TableField("top_end_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime topEndTime;

	/**
	 * 任务审核驳回或者被封，保存原因
	 */
	@TableField("reason")
	private String reason;
}
