package com.chxt.db.transaction.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.mapper.TransactionLogMapper;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.repository.TransactionLogRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TransactionLogRepositoryImpl extends ServiceImpl<TransactionLogMapper, TransactionLogPO> implements TransactionLogRepository {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(TransactionChannel channel) {
        LambdaQueryChainWrapper<TransactionLogPO> query = this.lambdaQuery()
                .eq(TransactionLogPO::getUserId, channel.getUserId())
                .eq(TransactionLogPO::getChannel, channel.getChannel())
                .in(TransactionLogPO::getDate, channel.getDateStrList());
        this.remove(query);

        List<TransactionLog> logs = channel.getLogs();
        List<TransactionLogPO> transactionLogPO = TransactionConvert.INSTANCE.toTransactionLogPO(logs);
        this.saveBatch(transactionLogPO);

    }
}
