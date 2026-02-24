package com.chxt.db.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("transaction_tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTagPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long transactionId;

    private String type;

    private String tag;
}
