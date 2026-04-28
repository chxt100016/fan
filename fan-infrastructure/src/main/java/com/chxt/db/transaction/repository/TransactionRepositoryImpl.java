package com.chxt.db.transaction.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionPO;
import com.chxt.db.transaction.entity.TransactionRelationPO;
import com.chxt.db.transaction.entity.TransactionTagPO;
import com.chxt.db.transaction.mapper.TransactionMapper;
import com.chxt.db.transaction.service.TransactionRelationRepositoryService;
import com.chxt.db.transaction.service.TransactionTagRepositoryService;
import com.chxt.domain.transaction.gateway.TransactionLogRepository;
import com.chxt.domain.transaction.gateway.TransactionRepository;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import com.chxt.domain.transaction.model.vo.TransactionTagVO;
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
public class TransactionRepositoryImpl extends ServiceImpl<TransactionMapper, TransactionPO> implements TransactionRepository {

    @Resource
    private TransactionRelationRepositoryService transactionRelationRepositoryService;

    @Resource
    private TransactionTagRepositoryService transactionTagRepositoryService;

    @Resource
    private TransactionLogRepository transactionLogRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(AnalysisParamVO param, List<Transaction> transactions) {
        // remove exist
        List<TransactionPO> exist = this.lambdaQuery().eq(TransactionPO::getUserId, param.getUserId()).ge(TransactionPO::getDate, param.getStartTime()).le(TransactionPO::getDate, param.getEndTime()).list();
        if (CollectionUtils.isNotEmpty(exist)) {
            List<String> transactionIds = exist.stream().map(TransactionPO::getTransactionId).toList();
            this.lambdaUpdate().in(TransactionPO::getTransactionId, transactionIds).remove();
            this.transactionRelationRepositoryService.lambdaUpdate().in(TransactionRelationPO::getTransactionId, transactionIds).remove();
            this.transactionTagRepositoryService.lambdaUpdate().in(TransactionTagPO::getTransactionId, transactionIds).remove();
        }


        List<TransactionPO> transactionPOList = TransactionConvert.INSTANCE.toTransactionPO(transactions);
        this.saveBatch(transactionPOList);

        List<TransactionRelationPO> relationPOList = getTransactionRelationPOS(transactions);
        this.transactionRelationRepositoryService.saveBatch(relationPOList);

        List<TransactionTagPO> transactionTagPOList = transactions.stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getTags()))
                .flatMap(item -> item.getTags().stream().map(tag -> {
                    TransactionTagPO transactionTagPO = TransactionConvert.INSTANCE.toTransactionTagPO(tag);
                    transactionTagPO.setTransactionId(item.getTransactionId());
                    return transactionTagPO;
                }))
                .toList();
        if (CollectionUtils.isNotEmpty(transactionTagPOList)) {
            this.transactionTagRepositoryService.saveBatch(transactionTagPOList);
        }

    }

    @Override
    public List<Transaction> list(AnalysisParamVO param) {
        List<TransactionPO> transactionPOList = this.lambdaQuery()
                .eq(TransactionPO::getUserId, param.getUserId())
                .ge(param.getStartTime() != null, TransactionPO::getDate, param.getStartTime())
                .le(param.getEndTime() != null, TransactionPO::getDate, param.getEndTime()).list();
        if (CollectionUtils.isEmpty(transactionPOList)) {
            return List.of();
        }

        List<String> transactionIds = transactionPOList.stream().map(TransactionPO::getTransactionId).toList();
        List<TransactionRelationPO> relationPOList = this.transactionRelationRepositoryService.lambdaQuery()
                .in(TransactionRelationPO::getTransactionId, transactionIds)
                .list();
        List<TransactionTagPO> tagPOList = this.transactionTagRepositoryService.lambdaQuery()
                .in(TransactionTagPO::getTransactionId, transactionIds)
                .list();

        List<String> transactionLogIds = relationPOList.stream().map(TransactionRelationPO::getTransactionLogId).toList();
        List<TransactionLog> transactionLogs = this.transactionLogRepository.list(transactionLogIds);
        Map<String, TransactionLog> logMap = transactionLogs.stream()
                .collect(Collectors.toMap(TransactionLog::getLogId, Function.identity(), (left, right) -> left));
        Map<String, List<TransactionTagVO>> transactionTagMap = tagPOList.stream()
                .collect(Collectors.groupingBy(
                        TransactionTagPO::getTransactionId,
                        Collectors.mapping(
                                item -> new TransactionTagVO(item.getType(), item.getTag(), item.getDescription()),
                                Collectors.toList()
                        )
                ));

        List<Transaction> transactions = new ArrayList<>(transactionPOList.size());
        Map<String, Transaction> transactionMap = new HashMap<>(transactionPOList.size());
        for (TransactionPO transactionPO : transactionPOList) {
            Transaction transaction = Transaction.builder()
                    .userId(transactionPO.getUserId())
                    .transactionId(transactionPO.getTransactionId())
                    .date(transactionPO.getDate())
                    .amount(transactionPO.getAmount())
                    .currency(transactionPO.getCurrency())
                    .type(transactionPO.getType())
                    .logMap(new HashMap<>())
                    .tags(new ArrayList<>(transactionTagMap.getOrDefault(transactionPO.getTransactionId(), List.of())))
                    .build();
            transactions.add(transaction);
            transactionMap.put(transactionPO.getTransactionId(), transaction);
        }

        if (CollectionUtils.isNotEmpty(relationPOList)) {
            for (TransactionRelationPO relationPO : relationPOList) {
                Transaction transaction = transactionMap.get(relationPO.getTransactionId());
                if (transaction == null) {
                    continue;
                }
                TransactionLog log = logMap.get(relationPO.getTransactionLogId());
                if (log == null) {
                    continue;
                }
                transaction.getLogMap().put(log.getChannel(), log);
            }
        }

        return transactions;
    }

    private List<TransactionRelationPO> getTransactionRelationPOS(List<Transaction> transactions) {
        List<TransactionRelationPO> relationPOList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            for (TransactionLog log : transaction.getLogMap().values()) {
                TransactionRelationPO relationPO = new TransactionRelationPO();
                relationPO.setTransactionId(transaction.getTransactionId());
                relationPO.setTransactionLogId(log.getLogId());
                relationPO.setType(TransactionEnums.Relation.ORIGINAL.getCode());
                relationPOList.add(relationPO);
            }
        }
        return relationPOList;
    }

} 
