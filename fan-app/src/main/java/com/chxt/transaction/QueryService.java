package com.chxt.transaction;

import com.chxt.domain.gateway.TransactionLogRepository;
import com.chxt.domain.gateway.TransactionRepository;
import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {


    @Resource
    private TransactionRepository transactionRepository;


    public List<String> getObsidian(AnalysisParamVO param) {
        List<Transaction> list = this.transactionRepository.list(param);
        return list.stream().map(Transaction::toObsidian).toList();

    }



}
