package com.chxt.domain.transaction.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 交易记录类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLog {

    /**
     * 交易日志id
     */
    private String logId;

    /**
     * 交易日期时间
     */
    private Date date;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    
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
    private String description;

    private String userId;

    /**
     * 交易对方
     */
    private String counterparty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionLog that = (TransactionLog) o;
        return Objects.equals(logId, that.logId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
}
