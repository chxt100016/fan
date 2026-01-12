package com.chxt.db.transaction.convert;

import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;
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


    TransactionLogPO toTransactionLogPO(TransactionLog transactionLog);

    List<TransactionLogPO> toTransactionLogPO(List<TransactionLog> transactionLogs);

 

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "channel", source = "transactionChannel.channel")
    @Mapping(target = "userId", source = "transactionChannel.userId")
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "count", source = "count", dateFormat = "yyyy-MM-dd")
    TransactionChannelLogPO toChannelLogPO(String date, Integer count, TransactionChannel transactionChannel);


    TransactionChannelLog toChannelLog(TransactionChannelLogPO po);
    List<TransactionChannelLog> toChannelLog(List<TransactionChannelLogPO> po);
}
