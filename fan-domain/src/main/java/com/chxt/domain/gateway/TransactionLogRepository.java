package com.chxt.domain.gateway;

import com.chxt.domain.transaction.model.entity.TransactionChannel;

public interface TransactionLogRepository {

    void batchAdd(TransactionChannel transactionChannel);


}
