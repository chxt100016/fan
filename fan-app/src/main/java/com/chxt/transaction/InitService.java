package com.chxt.transaction;


import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.domain.transaction.service.TransactionLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitService {

    @Resource
    private TransactionLogService transactionLogService;

    public void init(MailParseParamVO param) {
        this.transactionLogService.init(param);
    }

    public List<Transaction> analysis(AnalysisParamVO param) {
        return this.transactionLogService.analysisLog(param);
    }

}
