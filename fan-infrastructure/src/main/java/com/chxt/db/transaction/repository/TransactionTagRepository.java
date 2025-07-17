package com.chxt.db.transaction.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransationTagPO;
import com.chxt.db.transaction.mapper.TransactionTagMapper;

@Repository
public class TransactionTagRepository extends ServiceImpl<TransactionTagMapper, TransationTagPO> {

} 