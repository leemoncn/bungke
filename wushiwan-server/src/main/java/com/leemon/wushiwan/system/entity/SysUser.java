package com.leemon.wushiwan.system.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.entity.BaseEntity;
import com.leemon.wushiwan.enums.LoginStatus;
import com.leemon.wushiwan.enums.UserType;
import com.leemon.wushiwan.mapper.FinancialWithdrawalMapper;
import com.leemon.wushiwan.util.SpringUtil;
import com.leemon.wushiwan.util.UserUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

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
public class SysUser extends BaseEntity implements JSONSerializable {

	private static final long serialVersionUID = 1L;

	/**
	 * MD5后的支付密码
	 */
	@JSONField(serialize = false, deserialize = false)
	@TableField("pay_password")
	private String payPassword;

	/**
	 * 用户的上级id
	 */
	@TableField("superior_id")
	private Integer superiorId;

	/**
	 * 我作为代理商的分成级别
	 */
	@TableField("agency_id")
	private Integer agencyId;

	/**
	 * 代理商的id
	 */
	@TableField("partner_id")
	private Integer partnerId;

	@TableField("nickname")
	private String nickname;

	/**
	 * 三方登陆的头像地址
	 */
	@TableField("head_img_url")
	private String headImgUrl;

	/**
	 * 用户绑定的手机号
	 */
	@TableField("phone")
	private String phone;

	/**
	 * 邮箱
	 */
	@TableField("email")
	private String email;

	/**
	 * 最后一次登陆IP
	 */
	@TableField("login_ip")
	private String loginIp;

	/**
	 * 最后一次登陆日期
	 */
	@TableField("login_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime loginTime;

	/**
	 * 登陆状态，比如允许登陆、禁止登陆、需绑定手机才可以登陆等等
	 */
	@TableField("login_status_property_id")
	private LoginStatus loginStatusPropertyId;

	/**
	 * 用户类型
	 */
	@TableField("user_type")
	private UserType userType;

	/**
	 * 保证金，单位为分
	 */
	@TableField("deposit")
	private Integer deposit;

	/**
	 * 任务币，单位分
	 */
	@TableField("mission_coin")
	private Integer missionCoin;

	/**
	 * 我当前收入，包括任务收入和分红，单位分
	 */
	@TableField("earning")
	private Integer earning;

	/**
	 * 支付宝账号
	 */
	@TableField("alipay")
	private String alipay;

	/**
	 * 真实姓名
	 */
	@TableField("real_name")
	private String realName;

	/**
	 * push的id
	 */
	@TableField(value = "push_id", strategy = FieldStrategy.IGNORED)
	private String pushId;

	/**
	 * 设备信息
	 */
	@TableField("device_info")
	private String deviceInfo;

	/**
	 * 前台返回的属性
	 *
	 * @return
	 */
	protected JSONObject createClientJsonObject() {
		JSONObject json = new JSONObject();
		json.put("headImgUrl", getHeadImgUrl());
		json.put("phone", getPhone());
		json.put("email", getEmail());
		json.put("deposit", getDeposit());
		json.put("missionCoin", getMissionCoin());
		json.put("earning", getEarning());
		json.put("id", getId());
		json.put("nickname", getNickname());
		if (getId() != null) {
			FinancialWithdrawalMapper withdrawalMapper = SpringUtil.getBean(FinancialWithdrawalMapper.class);
			json.put("moneyInReview", withdrawalMapper.selectUserInReviewMoney(getId()));
		}
		json.put("partnerId", getPartnerId());
		json.put("myAgencyId", getAgencyId());
		json.put("superiorId", getSuperiorId());
		json.put("alipay", getAlipay());
		json.put("realName", getRealName());
		return json;
	}

	protected void addServerJsonObject(JSONObject json) {
		json.put("loginIp", getLoginIp());
		json.put("loginTime", getLoginTime() == null ? null : getLoginTime().toString().replace("T", " "));
		json.put("loginStatusPropertyId", getLoginStatusPropertyId());
		json.put("userType", getUserType());
		json.put("pushId", getPushId());
		json.put("deviceInfo", getDeviceInfo());
	}

	@Override
	public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
		if (UserUtil.getSysUser().getUserType() == UserType.CLIENT) {
			JSONObject json = createClientJsonObject();
			serializer.write(json);
			return;
		}
		//后台返回的属性,包含前台属性
		JSONObject json = createClientJsonObject();
		addServerJsonObject(json);
		serializer.write(json);
	}
}
