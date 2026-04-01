package com.chxt.db.transaction.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionPO;
import com.chxt.db.transaction.entity.TransactionRelationPO;
import com.chxt.db.transaction.mapper.TransactionMapper;
import com.chxt.domain.gateway.TransactionLogRepository;
import com.chxt.domain.gateway.TransactionRepository;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class TransactionRepositoryService extends ServiceImpl<TransactionMapper, TransactionPO>{


} 
