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
public class PayPaymentMode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    private Integer userId;

    /**
     * 绑定的提现方式ID，比如支付宝、微信
     */
    @TableField("type_property_id")
    private Integer typePropertyId;

    /**
     * 真实姓名
     */
    @TableField("name")
    private String name;

    /**
     * 账号
     */
    @TableField("account")
    private String account;


}
