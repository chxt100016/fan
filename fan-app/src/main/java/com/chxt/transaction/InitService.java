package com.chxt.transaction;



import java.util.List;

import org.springframework.stereotype.Service;

import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.repository.TransactionChannelLogRepository;
import com.chxt.db.transaction.repository.TransactionLogRepository;
import com.chxt.domain.transaction.TransactionService;
import com.chxt.domain.transaction.entity.TransactionChannel;

import jakarta.annotation.Resource;

@Service
public class InitService {
 

    @Resource
    private TransactionLogRepository transactionLogRepository;

    @Resource
    private TransactionChannelLogRepository transactionChannelLogRepository;

    public void init() {
        List<TransactionChannel> init = TransactionService.init();

        for (TransactionChannel item : init) {
            // delete old data
            transactionChannelLogRepository.delByDayChannel(item.getChannel(), item.getDateRanges());
            transactionLogRepository.delByDayChannel(item.getChannel(), item.getDateRanges());

            // channelLogs
            item.getDayCountMap().forEach((key, value) -> {
                TransactionChannelLogPO transactionChannelLogPO = TransactionConvert.INSTANCE.toPO(key, value, item);
                transactionChannelLogRepository.save(transactionChannelLogPO);
            });

            // logs
            List<TransactionLogPO> transactionLogPOList = TransactionConvert.INSTANCE.toTransactionLogPOList(item.getLogs());
            transactionLogRepository.saveBatch(transactionLogPOList);
        }
    }

}
