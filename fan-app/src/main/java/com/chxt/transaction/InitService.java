package com.chxt.transaction;


import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.repository.TransactionChannelLogRepository;
import com.chxt.db.transaction.repository.TransactionLogRepository;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.domain.transaction.service.TransactionLogService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitService {

    @Resource
    private TransactionLogService transactionLogService;

    @Resource
    private TransactionLogRepository transactionLogRepository;

    @Resource
    private TransactionChannelLogRepository transactionChannelLogRepository;



    public void init(MailParseParamVO param) {

        List<TransactionChannel> init = transactionLogService.init(param);
		

        for (TransactionChannel item : init) {
			if (CollectionUtils.isEmpty(item.getLogs())) {
				continue;
			}
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
