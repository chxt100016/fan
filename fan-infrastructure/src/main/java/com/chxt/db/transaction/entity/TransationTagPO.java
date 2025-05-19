package com.chxt.db.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("transaction_tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransationTagPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long transactionId;

    private String tag;
}
