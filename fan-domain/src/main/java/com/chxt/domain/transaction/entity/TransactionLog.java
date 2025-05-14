package com.chxt.domain.transaction.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易记录类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLog {
    /**
     * 交易日期时间
     */
    private Date dateTime;
    
    /**
     * 交易金额
     */
    private Double amount;
    
    /**
     * 交易币种
     */
    private String currency;
    
    /**
     * 交易类型
     */
    private String type;

    /**
     * 收/付款方式
     */
    private String method;

    /**
     * 收/付款渠道
     */
    private String channel;

    /**
     * 交易描述
     */
    private String desc;
}
