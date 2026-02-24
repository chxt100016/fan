package com.chxt.db.transaction.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.mapper.TransactionLogMapper;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.gateway.TransactionLogRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TransactionLogRepositoryImpl extends ServiceImpl<TransactionLogMapper, TransactionLogPO> implements TransactionLogRepository {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(TransactionChannel channel) {

        List<TransactionLog> logs = channel.getLogs();
        List<String> logIds = logs.stream().map(TransactionLog::getLogId).toList();
        List<TransactionLogPO> exist = this.lambdaQuery().in(TransactionLogPO::getLogId, logIds).list();
        if (exist.size() == logIds.size()) {
            return;
        }
        Set<String> existSet = new HashSet<>(exist.stream().map(TransactionLogPO::getLogId).toList());
        logs = logs.stream().filter(log -> !existSet.contains(log.getLogId())).collect(Collectors.toList());
        List<TransactionLogPO> transactionLogPO = TransactionConvert.INSTANCE.toTransactionLogPO(logs);
        this.saveBatch(transactionLogPO);

    }

    @Override
    public List<TransactionLog> list(String userId, String startDate, String endDate) {
        List<TransactionLogPO> data = this.lambdaQuery()
                .eq(TransactionLogPO::getUserId, userId)
                .ge(TransactionLogPO::getDate, startDate)
                .le(TransactionLogPO::getDate, endDate)
                .list();
        return TransactionConvert.INSTANCE.toTransactionLogs(data);
    }

    @Override
    public List<TransactionLog> list(List<String> logIds) {
        List<TransactionLogPO> data = this.lambdaQuery()
                .in(TransactionLogPO::getLogId, logIds)
                .list();
        return TransactionConvert.INSTANCE.toTransactionLogs(data);
    }


}
