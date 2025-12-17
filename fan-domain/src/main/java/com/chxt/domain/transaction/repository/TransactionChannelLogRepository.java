package com.chxt.domain.transaction.repository;

import com.chxt.domain.transaction.model.entity.TransactionChannel;

import java.util.List;

public interface TransactionChannelLogRepository {


    void delByDayChannel(String channel, List<String[]> dateStrList);

    void add(TransactionChannel item);
}
