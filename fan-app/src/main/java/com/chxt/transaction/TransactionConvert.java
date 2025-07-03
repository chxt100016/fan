package com.chxt.transaction;

import java.util.List;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;

@Mapper
public interface TransactionConvert {
    
    TransactionConvert INSTANCE = Mappers.getMapper(TransactionConvert.class);

    @Mapping(target = "id", ignore = true)
    TransactionLogPO toTransactionLogPO(TransactionLog transactionLog);

    List<TransactionLogPO> toTransactionLogPOList(List<TransactionLog> transactionLogs);

 

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operationDate", source = "transactionChannel.operationDate")
    @Mapping(target = "channel", source = "transactionChannel.channel")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd")
    TransactionChannelLogPO toPO(String date, Integer count, TransactionChannel transactionChannel);
}
