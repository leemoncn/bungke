package com.leemon.wushiwan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.LoginRegisterType;
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
public class CoreLoginAuth extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户表，用户ID
	 */
	@TableField("user_id")
	private Integer userId;

	/**
	 * 登陆账号，如果是三方登陆的话，应该是三方的id
	 */
	@TableField("account")
	private String account;

	/**
	 * 登陆的密码，如果是三方登陆的话，有可能是token，存储的是加密后的密码
	 */
	@TableField("password")
	private String password;

	/**
	 * 登陆类型，属性表的id
	 */
	@TableField("type_property_id")
	private LoginRegisterType typePropertyId;

	/**
	 * 某些三方登录返回的json
	 */
	@TableField("data")
	private String data;

	/**
	 * pushid
	 */
	@TableField(exist = false)
	private String pushId;

	/**
	 * deviceInfo
	 */
	@TableField(exist = false)
	private String deviceInfo;
}
