package com.chxt.db.transaction.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("transaction_channel_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChannelLogPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String channel;

    private Date date;

    private Integer count;

    private Date createTime;

    private Date updateTime;

}
