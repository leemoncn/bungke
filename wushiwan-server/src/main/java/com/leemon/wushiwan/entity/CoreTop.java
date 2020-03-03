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
 * @since 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CoreTop extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 任务ID
     */
    @TableField("mission_id")
    private Integer missionId;

    /**
     * 免费小时数
     */
    @TableField("free_hours")
    private Integer freeHours;

    /**
     * 收费的小时数
     */
    @TableField("paid_hours")
    private Integer paidHours;

    /**
     * 收费小时数花费的钱
     */
    @TableField("price")
    private Integer price;

    /**
     * 置顶结束时间
     */
    @TableField("top_end_time")
    private LocalDateTime topEndTime;


}
