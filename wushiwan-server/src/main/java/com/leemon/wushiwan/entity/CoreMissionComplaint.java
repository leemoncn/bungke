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
 * @since 2019-06-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreMissionComplaint extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("mission_accept_id")
	private Integer missionAcceptId;

	/**
	 * 任务申诉者的id
	 */
	@TableField("complainter_user_id")
	private Integer complainterUserId;

	/**
	 * 申诉内容
	 */
	@TableField("text")
	private String text;

	/**
	 * 申诉是否成功，1代表申诉成功
	 */
	@TableField("result")
	private Boolean result;


}
