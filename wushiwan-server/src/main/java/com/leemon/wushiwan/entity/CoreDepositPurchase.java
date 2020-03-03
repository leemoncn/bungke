package com.leemon.wushiwan.entity;

import com.leemon.wushiwan.entity.BaseEntity;

import java.time.LocalDateTime;

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
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreDepositPurchase extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableField("user_id")
	private Integer userId;

	/**
	 * 保证金金额,单位分
	 */
	@TableField("deposit")
	private Integer deposit;

	/**
	 * 申退保证金日期
	 */
	@TableField("refund_time")
	private LocalDateTime refundTime;

	/**
	 * 申退保证金完成日期
	 */
	@TableField("refund_finish_time")
	private LocalDateTime refundFinishTime;

	/**
	 * 当前合作商是否可用
	 */
	@TableField("usable")
	private Boolean usable;


}
