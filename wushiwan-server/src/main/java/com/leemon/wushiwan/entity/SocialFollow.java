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
public class SocialFollow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关注者
     */
    @TableField("from_user_id")
    private Integer fromUserId;

    /**
     * 被关注者
     */
    @TableField("to_user_id")
    private Integer toUserId;


}
