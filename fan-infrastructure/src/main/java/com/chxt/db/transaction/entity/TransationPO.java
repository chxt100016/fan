package com.chxt.db.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransationPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String amount;

    private String currency;

    private String type;


}
