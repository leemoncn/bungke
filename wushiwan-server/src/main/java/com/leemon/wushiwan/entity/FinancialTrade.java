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
 * @since 2019-07-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class FinancialTrade extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    private Integer userId;

    /**
     * 单号
     */
    @TableField("trade_no")
    private String tradeNo;

    /**
     * 金额，单位分
     */
    @TableField("amount")
    private Integer amount;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 内容
     */
    @TableField("description")
    private String description;

    /**
     * 第三方订单号
     */
    @TableField("third_trade_no")
    private String thirdTradeNo;

    /**
     * 交易状态,0是未完成，1是已完成
     */
    @TableField("finished")
    private Boolean finished;

    /**
     * 支付完成时间
     */
    @TableField("finish_time")
    private LocalDateTime finishTime;


}
