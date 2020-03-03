package com.leemon.wushiwan.entity;

import com.leemon.wushiwan.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author leemon
 * @since 2019-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreVersion extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 版本号
	 */
	@TableField("version_code")
	private Integer versionCode;

	/**
	 * 版本名
	 */
	@TableField("version_name")
	private String versionName;

	/**
	 * wgt文件
	 */
	@TableField("wgt_filename")
	private String wgtFilename;

	/**
	 * 强制更新
	 */
	@TableField("force_update")
	private Boolean forceUpdate;

	/**
	 * wgt和html是否部署完成
	 */
	@TableField("deployed")
	private Boolean deployed;
}
