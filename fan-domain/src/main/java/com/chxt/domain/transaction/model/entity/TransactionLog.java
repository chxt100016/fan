package com.chxt.domain.transaction.model.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.chxt.domain.transaction.model.constants.TransactionEnums;

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

    /**
     * 交易日志id
     */
    private String logId;

    public boolean canMerge(TransactionLog other) {
        if (this.channel.equals(other.getChannel())) {
            return false;
        }
        if (this.getAmount().compareTo(other.getAmount()) != 0) {
            return false;
        }
        if (this.getCurrency().equals(other.getCurrency())) {
            return false;
        }
        if (this.getType().equals(other.getType())) {
            return false;
        }
        if (Math.abs(this.getDate().getTime() - other.getDate().getTime()) > 1000 * 30) {
            return false;
        }
        return true;
    }

    public void printLog() {
        String dateStr = DateFormatUtils.format(this.date, "yyyy-MM-dd");
        System.out.println(dateStr + " " + TransactionEnums.TYPE.getByCode(this.type).getName());
        System.out.println(this.amount + " " + this.currency);
        System.out.println(this.description);
        System.out.println("--------------------------------");
    }

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
