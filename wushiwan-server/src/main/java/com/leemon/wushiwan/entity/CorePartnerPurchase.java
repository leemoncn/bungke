package com.leemon.wushiwan.entity;

import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.PartnerTimeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author leemon
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CorePartnerPurchase extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableField("user_id")
	private Integer userId;

	/**
	 * 合作商id
	 */
	@TableField("partner_id")
	private Integer partnerId;

	/**
	 * 花费的钱
	 */
	@TableField("price")
	private Integer price;

	/**
	 * 合作商开始日期
	 */
	@TableField("start_time")
	private LocalDate startTime;

	/**
	 * 合作商结束日期
	 */
	@TableField("end_time")
	private LocalDate endTime;

	/**
	 * 购买的时间类型
	 */
	@TableField("time_type")
	private PartnerTimeType timeType;

	/**
	 * 是否可用
	 */
	@TableField("usable")
	private Boolean usable;
}
