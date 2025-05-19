package com.chxt.domain.transaction.entity;


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
     * 记录类型 
     */
    private String type;

    /**
     * 
     */
    private List<TransactionLog> recordSourceList;
  
    

} 