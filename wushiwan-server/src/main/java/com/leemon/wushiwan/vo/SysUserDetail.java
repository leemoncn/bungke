package com.leemon.wushiwan.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.UserType;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.util.UserUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @description:
 * @author: limeng
 * @create: 2019-08-14 21:57
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserDetail extends SysUser implements Serializable, JSONSerializable {
	private String partnerName;
	/**
	 * 充值是平台给出的返现提成,返给上级，百分比，乘以100
	 */
	@TableField("recharge_by_app")
	private Integer rechargeByApp;

	/**
	 * 提现时用户从自己的钱拿出来给上级返现的比例
	 */
	@TableField("withdraw_by_user")
	private Integer withdrawByUser;

	@Override
	public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
		/**
		 * 后台返回的属性,包含前台属性
		 */
		JSONObject json = createClientJsonObject();
		addServerJsonObject(json);
		json.put("partnerName", getPartnerName());
		json.put("rechargeByApp", getRechargeByApp());
		json.put("withdrawByUser", getWithdrawByUser());
		serializer.write(json);
	}
}
