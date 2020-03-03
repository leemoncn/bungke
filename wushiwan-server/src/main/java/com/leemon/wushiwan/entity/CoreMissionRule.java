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
public class CoreMissionRule extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务类型的属性表ID
	 */
	@TableField("type_property_id")
	private Integer typePropertyId;

	/**
	 * 最低出价，单位为分
	 */
	@TableField("min_price")
	private Integer minPrice;

	/**
	 * 最低任务数量
	 */
	@TableField("min_count")
	private Integer minCount;

	/**
	 * 审核验证图最多上传数量
	 */
	@TableField("verify_img_count")
	private Integer verifyImgCount;

	/**
	 * 是否可用。如果不可用，则任务列表不显示。已接取的还可以完成
	 */
	@TableField("usable")
	private Boolean usable;

}
