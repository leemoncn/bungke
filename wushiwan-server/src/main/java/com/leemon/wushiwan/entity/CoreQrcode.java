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
 * @since 2019-04-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreQrcode extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("user_id")
	private Integer userId;

	/**
	 * 生成的二维码图片在服务器本地的路径
	 */
	@TableField("uuid")
	private String uuid;

	/**
	 * 到期时间
	 */
	@TableField("expire_time")
	private LocalDateTime expireTime;


}
