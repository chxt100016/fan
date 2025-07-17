package com.chxt.db.transaction.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TransactionChannelPO;
import com.chxt.db.transaction.mapper.TransactionChannelMapper;

@Repository
public class TransactionChannelRepository extends ServiceImpl<TransactionChannelMapper, TransactionChannelPO> {

} 