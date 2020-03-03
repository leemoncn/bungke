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
public class CoreImg extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 具体的ID,比如发布任务是core_mission_detail,提交任务是core_mission_accept的id,意见反馈是sys_advice的id
	 */
	@TableField("data_id")
	private Integer dataId;

	/**
	 * 任务发布者的ID，或者任务接取者的ID
	 */
	@TableField("user_id")
	private Integer userId;

	/**
	 * 图片的本地路径
	 */
	@TableField("path")
	private String path;

	/**
	 * 此字段主要是为了区分同一个id却有多个需要区分的图片，比如创建任务，需要上传审核图片和操作图片
	 */
	@TableField("type")
	private Integer type;


}
