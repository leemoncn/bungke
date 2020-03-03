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
 * @since 2019-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreMissionDetailStep extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("mission_detail_id")
    private Integer missionDetailId;

    /**
     * 是否包含图片
     */
    @TableField("haveImg")
    private Boolean haveImg;

    /**
     * 文字
     */
    @TableField("text")
    private String text;


}
