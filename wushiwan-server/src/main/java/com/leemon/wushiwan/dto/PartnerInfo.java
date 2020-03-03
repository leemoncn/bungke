package com.leemon.wushiwan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.entity.CorePartnerPurchase;
import com.leemon.wushiwan.enums.PartnerTimeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @description: 此类中的id、createTime等共有属性为CorePartnerPurchase中的
 * @author: limeng
 * @create: 2019-06-29 10:27
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class PartnerInfo extends CorePartner {

	//这里面的id是CorePartnerPurchase的id

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
