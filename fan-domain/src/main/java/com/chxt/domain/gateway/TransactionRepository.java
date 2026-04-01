package com.chxt.domain.gateway;

import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;

import java.util.List;

public interface TransactionRepository {


    void batchAdd(AnalysisParamVO param, List<Transaction> transactions);

    List<Transaction> list(AnalysisParamVO param);
}
