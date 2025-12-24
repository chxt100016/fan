package com.chxt.domain.transaction.repository;

import com.chxt.domain.transaction.model.entity.TransactionChannel;

import java.util.List;

public interface TransactionChannelLogRepository {


    void batchAdd(TransactionChannel item);
}
