package com.chxt.db.transaction.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.mapper.TransactionLogMapper;

import java.util.List;

@Repository
public class TransactionLogRepository extends ServiceImpl<TransactionLogMapper, TransactionLogPO> {

    public void delByDayChannel(String channel, List<String[]> dateRanges) {
        if (dateRanges == null || dateRanges.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<TransactionLogPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransactionLogPO::getChannel, channel);
        for (String[] item : dateRanges) {
            wrapper.and(i -> i.between(TransactionLogPO::getDate, item[0], item[1]));
        }
        this.remove(wrapper);
    }

}
