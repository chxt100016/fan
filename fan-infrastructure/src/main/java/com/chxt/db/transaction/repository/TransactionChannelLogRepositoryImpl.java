package com.chxt.db.transaction.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.mapper.TransactionChannelLogMapper;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.repository.TransactionChannelLogRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TransactionChannelLogRepositoryImpl extends ServiceImpl<TransactionChannelLogMapper, TransactionChannelLogPO> implements TransactionChannelLogRepository {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(TransactionChannel channel) {
        LambdaQueryChainWrapper<TransactionChannelLogPO> query = this.lambdaQuery()
                .eq(TransactionChannelLogPO::getUserId, channel.getUserId())
                .eq(TransactionChannelLogPO::getChannel, channel.getChannel())
                .in(TransactionChannelLogPO::getDate, channel.getDateStrList());
        this.remove(query);

        List<TransactionChannelLogPO> list = channel.getDayCountMap().entrySet().stream().map(item -> TransactionConvert.INSTANCE.toChannelLogPO(item.getKey(), item.getValue(), channel)).toList();
        this.saveBatch(list);
    }

} 