package com.chxt.db.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("transaction_channel")
public class TransactionChannelPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long transactionId;

    private String channel;

    private String parent;



}
