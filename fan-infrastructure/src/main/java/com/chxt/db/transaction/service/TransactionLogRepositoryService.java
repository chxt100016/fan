package com.chxt.db.transaction.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionLogPO;
import com.chxt.db.transaction.mapper.TransactionLogMapper;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.gateway.TransactionLogRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TransactionLogRepositoryService extends ServiceImpl<TransactionLogMapper, TransactionLogPO> {


}
