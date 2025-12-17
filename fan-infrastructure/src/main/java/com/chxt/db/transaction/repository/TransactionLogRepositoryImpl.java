package com.chxt.db.transaction.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.mapper.TransactionLogMapper;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.repository.TransactionLogRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TransactionLogRepositoryImpl extends ServiceImpl<TransactionLogMapper, TransactionLogPO> implements TransactionLogRepository {

    @Override
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(List<TransactionLog> logs) {
        List<TransactionLogPO> data = TransactionConvert.INSTANCE.toTransactionLogPO(logs);
        this.saveBatch(data);
    }

}
