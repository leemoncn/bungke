package com.leemon.wushiwan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.entity.BaseEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author leemon
 * @since 2019-06-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class FinancialWithdrawal extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("user_id")
	private Integer userId;

	/**
	 * 货币转换为人民币的数值（去掉给上级的分成后），单位分
	 */
	@TableField("money")
	private Integer money;

	/**
	 * 货币变化的原因
	 */
	@TableField("reason_property_id")
	private CurrencyChangeReasonType reasonPropertyId;

	/**
	 * 提现申请是否批准
	 */
	@TableField("approve")
	private Boolean approve;

	/**
	 * 提现批准时间
	 */
	@TableField("approve_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime approveTime;

	/**
	 * 批准人
	 */
	@TableField("approve_user_id")
	private Integer approveUserId;


}
