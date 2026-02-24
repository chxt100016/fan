package com.chxt.db.transaction.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransactionRelationPO;
import com.chxt.db.transaction.mapper.TransactionRelationMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRelationRepository extends ServiceImpl<TransactionRelationMapper, TransactionRelationPO> {

} 