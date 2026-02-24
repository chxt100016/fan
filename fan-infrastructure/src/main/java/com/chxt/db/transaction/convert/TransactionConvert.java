package com.chxt.db.transaction.convert;

import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.entity.TransactionPO;
import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionChannelLog;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionConvert {
    
    TransactionConvert INSTANCE = Mappers.getMapper(TransactionConvert.class);

    TransactionLog toTransactionLog(TransactionLogPO transactionLogPO);
    List<TransactionLog> toTransactionLogs(List<TransactionLogPO> transactionLogPO);


    TransactionLogPO toTransactionLogPO(TransactionLog transactionLog);
    List<TransactionLogPO> toTransactionLogPO(List<TransactionLog> transactionLogs);

 

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "channel", source = "transactionChannel.channel")
    @Mapping(target = "userId", source = "transactionChannel.userId")
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "count", source = "count")
    TransactionChannelLogPO toChannelLogPO(String date, Integer count, TransactionChannel transactionChannel);


    TransactionChannelLog toChannelLog(TransactionChannelLogPO po);
    List<TransactionChannelLog> toChannelLog(List<TransactionChannelLogPO> po);

    TransactionPO toTransactionPO(Transaction transaction);
    List<TransactionPO> toTransactionPO(List<Transaction> transaction);
}
