package com.chxt.transaction;



import java.util.List;

import org.springframework.stereotype.Service;

import com.chxt.db.transaction.TransactionRepository;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.domain.transaction.TransactionService;
import com.chxt.domain.transaction.entity.TransactionChannel;

import jakarta.annotation.Resource;

@Service
public class InitService {
 

    @Resource
    private TransactionRepository transactionRepository;

    public void init() {
        List<TransactionChannel> init = TransactionService.init();
        init.forEach(item -> {
            List<TransactionLogPO> transactionLogPOList = TransactionConvert.INSTANCE.toTransactionLogPOList(item.getLogs());
            transactionRepository.saveBatch(transactionLogPOList);
        });
    }

}
