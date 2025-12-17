package com.chxt.domain.transaction.repository;

import com.chxt.domain.transaction.model.entity.TransactionLog;

import java.util.List;

public interface TransactionLogRepository {

    void delByDayChannel(String channel, List<String[]> dateRanges);

    void add(List<TransactionLog> logs);
}
