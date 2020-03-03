package com.leemon.wushiwan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.entity.BaseEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.MissionProceedType;
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
public class CoreMissionAccept extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 接取的任务ID
	 */
	@TableField("mission_id")
	private Integer missionId;

	/**
	 * 接单者ID
	 */
	@TableField("accept_user_id")
	private Integer acceptUserId;

	/**
	 * 发单者ID
	 */
	@TableField("publish_user_id")
	private Integer publishUserId;

	/**
	 * 接受任务时间
	 */
	@TableField("accept_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime acceptTime;

	/**
	 * 完成任务时间，或者任务不合格时，12小时内用户没有再次上传，12小时候就会填上完成时间
	 */
	@TableField("finish_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime finishTime;

	/**
	 * 提交任务时间,每次提交都会更新
	 */
	@TableField("upload_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime uploadTime;

	/**
	 * 商家审核任务时间,每次审核都会更新
	 */
	@TableField("review_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime reviewTime;

	/**
	 * 任务执行情况
	 */
	@TableField("proceed_property_id")
	private MissionProceedType proceedPropertyId;

	/**
	 * 提交任务时的文字验证
	 */
	@TableField("text_verify")
	private String textVerify;

}
