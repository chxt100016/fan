package com.chxt.db.transaction.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransationPO;
import com.chxt.db.transaction.mapper.TransactionMapper;

@Repository
public class TransactionRepository extends ServiceImpl<TransactionMapper, TransationPO> {

} 