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
 * @since 2019-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class FinancialTransfer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 申请提现用户的id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * FinancialWithdrawal提现申请表的id
     */
    @TableField("withdrawal_id")
    private Integer withdrawalId;

    /**
     * 第三方支付的转账单号
     */
    @TableField("third_transfer_no")
    private String thirdTransferNo;

    /**
     * 系统生成的转账单号
     */
    @TableField("transfer_no")
    private String transferNo;


}
