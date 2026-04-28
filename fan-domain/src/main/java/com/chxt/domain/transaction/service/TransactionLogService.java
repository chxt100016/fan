package com.chxt.domain.transaction.service;

import com.chxt.domain.transaction.gateway.TransactionLogRepository;
import com.chxt.domain.transaction.gateway.TransactionRepository;
import com.chxt.domain.transaction.model.entity.RecordParser;
import com.chxt.domain.transaction.model.entity.MailPicker;
import com.chxt.domain.transaction.component.TransactionEntityRepository;
import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import com.chxt.domain.transaction.model.vo.ChannelMailVO;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TransactionLogService {

    @Resource
    private TransactionEntityRepository userMailFactory;

    @Resource
    private TransactionLogRepository transactionLogRepository;

    @Resource
    private TransactionRepository transactionRepository;

    @SneakyThrows
    public void init(MailParseParamVO param) {
        MailPicker picker = this.userMailFactory.getPicker(param.getUserId(), param.getStartDate(), param.getEndDate(), param.getChannel());
        List<ChannelMailVO> mailList = picker.search();

        RecordParser parser = this.userMailFactory.getParser(param.getUserId(), param.getStartDate(), param.getEndDate(), param.getChannel());
        List<TransactionChannel> data = parser.parse(mailList).stream().filter(TransactionChannel::isSuccess).toList();
        this.transactionLogRepository.batchAdd(data);
    }

    public List<Transaction> analysisLog(AnalysisParamVO param) {
        List<TransactionChannel> channelList = this.transactionLogRepository.list(param.getUserId(), param.getStartTime(), param.getEndTime());
        List<TransactionLog> data = channelList.stream().flatMap(item -> item.getLogs().stream()).toList();
        List<Transaction> transactions = this.mergeAll(data);
        this.transactionRepository.batchAdd(param, transactions);
        return transactions;

    }

    public List<Transaction> mergeAll(List<TransactionLog> data) {
        List<TransactionLog> allLogs = data.stream()
                .sorted(Comparator.comparing(TransactionLog::getDate))
                .toList();

        List<Transaction> transactions = new ArrayList<>();

        for (TransactionLog log : allLogs) {
            boolean merged = false;
            // 倒序遍历（优先匹配最近的交易，效率更高）
            for (int i = transactions.size() - 1; i >= 0; i--) {
                Transaction tx = transactions.get(i);
                boolean useSameDayWindow = this.useSameDayWindow(tx, log);
                if (this.shouldBreakSearch(tx, log, useSameDayWindow)) {
                    break;
                }

                boolean currentMerged = useSameDayWindow ? tx.mergeInSameDay(log) : tx.merge(log);
                if (currentMerged) {
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                transactions.add(new Transaction(log));
            }
        }

        return transactions;
    }

    private boolean useSameDayWindow(Transaction tx, TransactionLog log) {
        return TransactionEnums.Channel.CMB_BANK.getCode().equals(log.getChannel()) || tx.containsChannel(TransactionEnums.Channel.CMB_BANK.getCode());
    }

    private boolean shouldBreakSearch(Transaction tx, TransactionLog log, boolean useSameDayWindow) {
        Date txDate = tx.getDate();
        Date logDate = log.getDate();
        if (txDate == null || logDate == null) {
            return true;
        }

        if (useSameDayWindow) {
            return !this.isSameDay(txDate, logDate) && txDate.before(this.startOfDay(logDate));
        }

        return logDate.getTime() - txDate.getTime() >= 10_000;
    }

    private Date startOfDay(Date date) {
        return new Date(org.apache.commons.lang3.time.DateUtils.truncate(date, java.util.Calendar.DAY_OF_MONTH).getTime());
    }

    private boolean isSameDay(Date d1, Date d2) {
        return org.apache.commons.lang3.time.DateUtils.isSameDay(d1, d2);
    }




    
}
