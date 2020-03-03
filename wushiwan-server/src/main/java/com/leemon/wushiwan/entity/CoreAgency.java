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
public class CoreAgency extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 充值是平台给出的返现提成，百分比，乘以100
	 */
	@TableField("recharge_by_app")
	private Integer rechargeByApp;

	/**
	 * 提现时用户从自己的钱拿出来给上级返现的比例
	 */
	@TableField("withdraw_by_user")
	private Integer withdrawByUser;


}
