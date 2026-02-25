package com.chxt.db.transaction.repository;

import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;

import com.chxt.db.transaction.service.TransactionChannelLogRepositoryService;
import com.chxt.db.transaction.service.TransactionLogRepositoryService;
import com.chxt.domain.gateway.TransactionLogRepository;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TransactionLogRepositoryImpl implements TransactionLogRepository {

    @Resource
    private TransactionChannelLogRepositoryService  transactionChannelLogRepositoryService;

    @Resource
    private TransactionLogRepositoryService transactionLogRepositoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(List<TransactionChannel> channelList) {
        for (TransactionChannel channel : channelList) {
            List<TransactionLog> logs = channel.getLogs();
            List<String> logIds = logs.stream().map(TransactionLog::getLogId).toList();
            List<TransactionLogPO> exist = this.transactionLogRepositoryService.lambdaQuery().in(TransactionLogPO::getLogId, logIds).list();
            if (exist.size() == logIds.size()) {
                return;
            }

            // channelLog
            this.transactionChannelLogRepositoryService.lambdaUpdate()
                    .eq(TransactionChannelLogPO::getUserId, channel.getUserId())
                    .eq(TransactionChannelLogPO::getChannel, channel.getChannel())
                    .in(TransactionChannelLogPO::getDate, channel.getDateStrList())
                    .remove();
            List<TransactionChannelLogPO> channelLogPOList = channel.getDayCountMap().entrySet().stream().map(item -> TransactionConvert.INSTANCE.toChannelLogPO(item.getKey(), item.getValue(), channel)).toList();
            channelLogPOList = channelLogPOList.stream().sorted(Comparator.comparing(TransactionChannelLogPO::getDate)).toList();
            this.transactionChannelLogRepositoryService.saveBatch(channelLogPOList);

            // log
            Set<String> existSet = new HashSet<>(exist.stream().map(TransactionLogPO::getLogId).toList());
            logs = logs.stream().filter(log -> !existSet.contains(log.getLogId())).collect(Collectors.toList());
            List<TransactionLogPO> transactionLogPO = TransactionConvert.INSTANCE.toTransactionLogPO(logs);
            this.transactionLogRepositoryService.saveBatch(transactionLogPO);
        }
    }

    @Override
    public List<TransactionLog> list(String userId, String startDate, String endDate) {
        List<TransactionLogPO> data = this.transactionLogRepositoryService.lambdaQuery()
                .eq(TransactionLogPO::getUserId, userId)
                .ge(TransactionLogPO::getDate, startDate)
                .le(TransactionLogPO::getDate, endDate)
                .list();
        return TransactionConvert.INSTANCE.toTransactionLogs(data);
    }

    @Override
    public List<TransactionLog> list(List<String> logIds) {
        List<TransactionLogPO> data = this.transactionLogRepositoryService.lambdaQuery()
                .in(TransactionLogPO::getLogId, logIds)
                .list();
        return TransactionConvert.INSTANCE.toTransactionLogs(data);
    }


}
