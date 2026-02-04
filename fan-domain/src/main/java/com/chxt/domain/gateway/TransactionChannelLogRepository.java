package com.chxt.domain.gateway;

import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionChannelLog;

import java.util.List;

public interface TransactionChannelLogRepository {


    void batchAdd(TransactionChannel item);

    List<TransactionChannelLog> listByChannel(String userId, String startDateStr, List<String> channel);

}
