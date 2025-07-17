package com.chxt.domain.transaction.model.entity;


import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单条消费记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    /**
     * 消费时间
     */
    private Date date;
    
    
    /**
     * 消费金额
     */
    private Double amount;
    
    
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
    private List<TransactionLog> logs;

    /**
     * 交易标签
     */
    private List<String> tags;
  
    // public Transactopn(List<TransactionLog> logs) {
    //     this.date = logs.stream().map(TransactionLog::getDate).min(Date::compareTo).orElse(null);
    //     this. amount
    // }

} 