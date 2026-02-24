package com.chxt.domain.transaction.model.entity;


import com.chxt.domain.transaction.model.constants.TransactionEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单条消费记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    private String userId;


    private String transactionId;

    /**
     * 消费时间
     */
    private Date date;
    
    
    /**
     * 消费金额
     */
    private BigDecimal amount;
    
    
    /**
     * 货币类型
     */
    private String currency;

    /**
     * 收支
     */
    private String type;

    /**
     * 交易记录
     */
    private Map<String, TransactionLog> logMap;

    /**
     * 交易标签
     */
    private List<String> tags;
  
    public Transaction(TransactionLog transactionLog) {
        this.userId = transactionLog.getUserId();
        this.date = transactionLog.getDate();
        this.amount = transactionLog.getAmount();
        this.currency = transactionLog.getCurrency();
        this.type = transactionLog.getType();
        this.logMap = new HashMap<>();
        logMap.put(transactionLog.getChannel(), transactionLog);
    }

    public boolean notInRange(Date date) {
        if (null == date) {
            return true;
        }

        return Math.abs(this.date.getTime() - date.getTime()) >= 10_000;
    }

    public boolean merge(TransactionLog transactionLog) {
        if (!this.canMerge(transactionLog)) {
            return false;
        }

        if (transactionLog.getDate().before(this.date)) {
            this.date = transactionLog.getDate();
        }
        this.logMap.put(transactionLog.getChannel(), transactionLog);

        return true;
    }

    private boolean canMerge(TransactionLog transactionLog) {
        if (logMap.containsKey(transactionLog.getChannel())) {
            return false;
        }

        if (this.notInRange(transactionLog.getDate())) {
            return false;
        }

        if (!this.amount.equals(transactionLog.getAmount())) {
            return false;
        }

        if (!this.currency.equals(transactionLog.getCurrency())) {
            return false;
        }

        if (!this.userId.equals(transactionLog.getUserId())) {
            return false;
        }

        return this.type.equals(transactionLog.getType());
    }

    public String getTransactionId() {
        return this.userId + ":" + this.date.getTime() + ":" + DigestUtils.md5Hex(this.logMap.values().stream().map(TransactionLog::getLogId).collect(Collectors.joining()));
    }

    public String toObsidian() {
        return "- " +
                this.obsidianItem("date", DateFormatUtils.format(this.date, "yyyy-MM-dd HH:mm:ss")) +
                this.logMap.keySet().stream().map(item -> this.obsidianItem("channel", TransactionEnums.Channel.getNameByCode(item))).collect(Collectors.joining()) +
                this.obsidianItem("amount", this.amount) +
                this.obsidianItem("currency", this.currency) +
                this.obsidianItem("type", TransactionEnums.Type.getByCode(this.type).getName());



    }

    private String obsidianItem(String key, Object value) {
        return "[" + key + "::" + value + "]";

    }
} 