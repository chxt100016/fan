package com.chxt.domain.transaction.repository;

import com.chxt.domain.transaction.model.entity.TransactionChannel;

public interface TransactionLogRepository {

    void batchAdd(TransactionChannel  transactionChannel);
}
