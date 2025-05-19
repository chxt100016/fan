package com.chxt.db.transaction;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransactionLogPO;

@Repository
public class TransactionRepository extends ServiceImpl<TransactionLogMapper, TransactionLogPO> {

}
