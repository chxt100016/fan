package com.chxt.db.transaction.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.mapper.TransactionLogMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionLogRepositoryService extends ServiceImpl<TransactionLogMapper, TransactionLogPO> {


}
