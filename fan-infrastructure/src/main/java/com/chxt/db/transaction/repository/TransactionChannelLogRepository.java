package com.chxt.db.transaction.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.mapper.TransactionChannelLogMapper;

@Repository
public class TransactionChannelLogRepository extends ServiceImpl<TransactionChannelLogMapper, TransactionChannelLogPO> {

    public void delByDayChannel(String channel, List<String[]> dateStrList) {
        if (dateStrList == null || dateStrList.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<TransactionChannelLogPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransactionChannelLogPO::getChannel, channel);
        for (String[] item : dateStrList) {
            wrapper.and(i -> i.between(TransactionChannelLogPO::getDate, item[0], item[1]));
        }
        this.remove(wrapper);
    }

} 