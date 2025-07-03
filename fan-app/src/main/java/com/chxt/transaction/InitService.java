package com.chxt.transaction;



import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.repository.TransactionChannelLogRepository;
import com.chxt.db.transaction.repository.TransactionLogRepository;
import com.chxt.domain.transaction.TransactionService;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.domain.transaction.parser.PasswordHelper;

import jakarta.annotation.Resource;

@Service
public class InitService {
 

    @Resource
    private TransactionLogRepository transactionLogRepository;

    @Resource
    private TransactionChannelLogRepository transactionChannelLogRepository;

	@Resource
	private PasswordHelper passwordHelper;

    public void init(MailParseParamVO param) {

        List<TransactionChannel> init = TransactionService.init(param, passwordHelper);
		

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
