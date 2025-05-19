package com.chxt.transaction;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.domain.transaction.entity.TransactionLog;

@Mapper
public interface TransactionConvert {
    
    TransactionConvert INSTANCE = Mappers.getMapper(TransactionConvert.class);

    TransactionLogPO toTransactionLogPO(TransactionLog transactionLog);

    List<TransactionLogPO> toTransactionLogPOList(List<TransactionLog> transactionLogs);

}
