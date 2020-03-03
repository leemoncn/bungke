package com.leemon.wushiwan.entity;

import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.CurrencyType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author leemon
 * @since 2019-06-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreCurrencyChange extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 货币变动值，可以正数也可以负数
	 */
	@TableField("record")
	private Integer record;

	/**
	 * 货币的类型
	 */
	@TableField("type_property_id")
	private CurrencyType typePropertyId;

	/**
	 * 货币变化原因
	 */
	@TableField("reason_property_id")
	private CurrencyChangeReasonType reasonPropertyId;

	/**
	 * 用户id
	 */
	@TableField("user_id")
	private Integer userId;


}
