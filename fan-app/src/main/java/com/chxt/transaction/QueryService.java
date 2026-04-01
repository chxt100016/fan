package com.chxt.transaction;

import com.chxt.domain.gateway.TransactionLogRepository;
import com.chxt.domain.gateway.TransactionRepository;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionDashboard;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryService {


    @Resource
    private TransactionRepository transactionRepository;

    @Resource
    private TransactionLogRepository transactionLogRepository;


    public List<String> getObsidian(AnalysisParamVO param) {
        List<Transaction> list = this.transactionRepository.list(param);
        return list.stream().map(Transaction::toObsidian).toList();

    }


    public List<String> getRangeStr(String userId) {
        List<TransactionChannel> list = this.transactionLogRepository.list(userId, null, null);
        return list.stream().flatMap(item -> item.getDateRanges().stream().map(a -> TransactionEnums.Channel.getNameByCode(item.getChannel()) + ":" + a.show()))
                .toList();
    }

    public TransactionDashboard getDashboard(String userId) {
        List<Transaction> list = this.transactionRepository.list(new AnalysisParamVO().setUserId(userId));
        return new TransactionDashboard(list);
    }

}
