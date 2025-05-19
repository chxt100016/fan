package com.chxt.db.transaction.entity;

import java.math.BigDecimal;
import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("transaction_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLogPO {

    @TableId(type = IdType.AUTO)
    private Long id;

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
}
