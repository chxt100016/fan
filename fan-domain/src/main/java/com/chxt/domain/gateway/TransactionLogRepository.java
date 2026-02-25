package com.chxt.domain.gateway;

import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;

import java.util.List;

public interface TransactionLogRepository {

    void batchAdd(List<TransactionChannel> transactionChannelList);

    List<TransactionLog> list(String userId, String startDate, String endDate);

    List<TransactionLog> list(List<String> logIds);
}
