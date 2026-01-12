package com.chxt.domain.transaction.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChannelLog {


    private Long id;

    private Long userId;

    private String channel;

    private Date date;

    private Integer count;

    private Date createTime;

    private Date updateTime;

}
