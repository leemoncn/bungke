package com.leemon.wushiwan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.entity.BaseEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class CoreMissionDetail extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 发布任务的类型，在属性表中的id
	 */
	@TableField("type_property_id")
	private Integer typePropertyId;

	/**
	 * 发布人id
	 */
	@TableField("user_id")
	private Integer userId;

	/**
	 * 属性表里面的支持设备类型
	 */
	@TableField("mobile_property_id")
	private Integer mobilePropertyId;

	/**
	 * 标题，12字以内
	 */
	@TableField("title")
	private String title;

	/**
	 * 截止时间
	 */
	@TableField("deadline_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deadlineTime;

	/**
	 * 出价，单位分
	 */
	@TableField("price")
	private Integer price;

	/**
	 * 剩余未完成的任务数量
	 */
	@TableField("count")
	private Integer count;

	/**
	 * 任务发布时的数量，发布后不会改变
	 */
	@TableField("publish_count")
	private Integer publishCount;

	/**
	 * 服务费金额，单位为分
	 */
	@TableField("fee_price")
	private Integer feePrice;

	/**
	 * 手续费的百分比，乘以100之后的数值
	 */
	@TableField("fee_percent")
	private Integer feePercent;

	/**
	 * 任务规则的id
	 */
	@TableField("mission_rule_id")
	private Integer missionRuleId;

	/**
	 * 链接
	 */
	@TableField("url")
	private String url;

	/**
	 * 文字验证
	 */
	@TableField("text_verify")
	private String textVerify;

	/**
	 * 备注
	 */
	@TableField("remark")
	private String remark;


}
