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
public class CorePartner extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 合作商的级别名字
	 */
	@TableField("name")
	private String name;

	/**
	 * 合作商级别，0是非合作商，级别越高数越大
	 */
	@TableField("level")
	private Integer level;

	/**
	 * 交易手续费的百分比，乘以100之后的数值
	 */
	@TableField("fee_percent")
	private Integer feePercent;

	/**
	 * 任务币的提现手续费，乘以100之后的数值
	 */
	@TableField("mission_payment_percent")
	private Integer missionPaymentPercent;

	/**
	 * 发布任务最低的服务费金额，单位分
	 */
	@TableField("min_fee_price")
	private Integer minFeePrice;

	/**
	 * 月卡价格，单位分/月
	 */
	@TableField("mouth_price")
	private Integer mouthPrice;

	/**
	 * 年卡价格，单位分/年
	 */
	@TableField("year_price")
	private Integer yearPrice;

	/**
	 * 免费推荐的时间，单位小时/日
	 */
	@TableField("ad_hour")
	private Integer adHour;
}
