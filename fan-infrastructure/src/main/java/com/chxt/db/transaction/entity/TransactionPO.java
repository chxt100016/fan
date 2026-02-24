package com.chxt.db.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@TableName("transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String transactionId;

    /**
     * 交易日期时间
     */
    private Date date;

    private BigDecimal amount;

    private String currency;

    private String type;


}
