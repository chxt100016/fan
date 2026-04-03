package com.chxt.db.transaction.repository;

import com.chxt.db.transaction.convert.TransactionConvert;
import com.chxt.db.transaction.entity.TransactionChannelLogPO;
import com.chxt.db.transaction.entity.TransactionLogPO;

import com.chxt.db.transaction.service.TransactionChannelLogRepositoryService;
import com.chxt.db.transaction.service.TransactionLogRepositoryService;
import com.chxt.domain.gateway.TransactionLogRepository;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.utils.DateRanges;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TransactionLogRepositoryImpl implements TransactionLogRepository {

    @Resource
    private TransactionChannelLogRepositoryService  transactionChannelLogRepositoryService;

    @Resource
    private TransactionLogRepositoryService transactionLogRepositoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(List<TransactionChannel> channelList) {
        for (TransactionChannel channel : channelList) {
            List<TransactionLog> logs = channel.getLogs();
            List<String> logIds = logs.stream().map(TransactionLog::getLogId).toList();
            List<TransactionLogPO> exist = this.transactionLogRepositoryService.lambdaQuery().in(TransactionLogPO::getLogId, logIds).list();
            if (exist.size() == logIds.size()) {
                continue;
            }

            Map<String, Integer> channelDayCount = buildChannelDayCount(channel);
            List<String> channelDateList = new ArrayList<>(channelDayCount.keySet());
            // channelLog
            this.transactionChannelLogRepositoryService.lambdaUpdate()
                    .eq(TransactionChannelLogPO::getUserId, channel.getUserId())
                    .eq(TransactionChannelLogPO::getChannel, channel.getChannel())
                    .in(CollectionUtils.isNotEmpty(channelDateList), TransactionChannelLogPO::getDate, channelDateList)
                    .remove();
            List<TransactionChannelLogPO> channelLogPOList = channelDayCount.entrySet().stream().map(item -> TransactionConvert.INSTANCE.toChannelLogPO(item.getKey(), item.getValue(), channel)).toList();
            channelLogPOList = channelLogPOList.stream().sorted(Comparator.comparing(TransactionChannelLogPO::getDate)).toList();
            if (CollectionUtils.isNotEmpty(channelLogPOList)) {
                this.transactionChannelLogRepositoryService.saveBatch(channelLogPOList);
            }

            // log
            Set<String> existSet = new HashSet<>(exist.stream().map(TransactionLogPO::getLogId).toList());
            logs = logs.stream().filter(log -> !existSet.contains(log.getLogId())).collect(Collectors.toList());
            List<TransactionLogPO> transactionLogPO = TransactionConvert.INSTANCE.toTransactionLogPO(logs);
            this.transactionLogRepositoryService.saveBatch(transactionLogPO);
        }
    }

    @Override
    public List<TransactionChannel> list(String userId, String startDate, String endDate) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        List<TransactionChannelLogPO> channelLogData = this.transactionChannelLogRepositoryService.lambdaQuery()
                .eq(TransactionChannelLogPO::getUserId, userId)
                .ge(Objects.nonNull(start), TransactionChannelLogPO::getDate, start)
                .le(Objects.nonNull(end), TransactionChannelLogPO::getDate, end)
                .orderByAsc(TransactionChannelLogPO::getDate)
                .list();

        List<TransactionLogPO> data = this.transactionLogRepositoryService.lambdaQuery()
                .eq(TransactionLogPO::getUserId, userId)
                .ge(Objects.nonNull(start), TransactionLogPO::getDate, start)
                .le(Objects.nonNull(end), TransactionLogPO::getDate, end)
                .orderByAsc(TransactionLogPO::getDate)
                .list();
        if (CollectionUtils.isEmpty(channelLogData) && CollectionUtils.isEmpty(data)) {
            return List.of();
        }

        Map<String, TransactionChannel> channelMap = new LinkedHashMap<>();
        for (TransactionChannelLogPO item : channelLogData) {
            TransactionChannel channel = channelMap.computeIfAbsent(
                    item.getChannel(),
                    key -> new TransactionChannel(userId, key)
            );
            channel.addDateRange(item.getDate(), item.getDate());
        }

        List<TransactionLog> logs = TransactionConvert.INSTANCE.toTransactionLogs(data);
        for (TransactionLog log : logs) {
            TransactionChannel channel = channelMap.computeIfAbsent(
                    log.getChannel(),
                    item -> new TransactionChannel(userId, item)
            );
            channel.addLogs(List.of(log));
        }
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public List<TransactionLog> list(List<String> logIds) {
        List<TransactionLogPO> data = this.transactionLogRepositoryService.lambdaQuery()
                .in(TransactionLogPO::getLogId, logIds)
                .list();
        return TransactionConvert.INSTANCE.toTransactionLogs(data);
    }

    private Map<String, Integer> buildChannelDayCount(TransactionChannel channel) {
        Map<String, Integer> result = new HashMap<>(channel.getDayCountMap());
        for (DateRanges.DateRange dateRange : channel.getDateRanges()) {
            Date current = DateUtils.truncate(dateRange.getStartDate(), Calendar.DATE);
            Date end = DateUtils.truncate(dateRange.getEndDate(), Calendar.DATE);
            while (!current.after(end)) {
                String dateStr = DateFormatUtils.format(current, "yyyy-MM-dd");
                result.putIfAbsent(dateStr, 0);
                current = DateUtils.addDays(current, 1);
            }
        }
        return result;
    }

    private Date parseDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return DateUtils.parseDate(dateStr, "yyyy-MM-dd");
        } catch (Exception e) {
            return null;
        }
    }

}
