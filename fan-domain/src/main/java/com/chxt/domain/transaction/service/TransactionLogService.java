package com.chxt.domain.transaction.service;

import com.chxt.domain.gateway.TransactionChannelLogRepository;
import com.chxt.domain.gateway.TransactionLogRepository;
import com.chxt.domain.gateway.TransactionRepository;
import com.chxt.domain.transaction.component.MailParser;
import com.chxt.domain.transaction.component.MailPicker;
import com.chxt.domain.transaction.component.UserMailFactory;
import com.chxt.domain.transaction.model.entity.Transaction;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import com.chxt.domain.transaction.model.vo.ChannelMail;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class TransactionLogService {

    @Resource
    private UserMailFactory userMailFactory;

    @Resource
    private TransactionLogRepository transactionLogRepository;

    @Resource
    private TransactionChannelLogRepository transactionChannelLogRepository;

    @Resource
    private TransactionRepository transactionRepository;

    @SneakyThrows
    public void init(MailParseParamVO param) {
        MailPicker picker = this.userMailFactory.getPicker(param.getUserId(), param.getStartDate(), param.getChannel());
        List<ChannelMail> mailList = picker.search();

        MailParser parser = this.userMailFactory.getParser(param.getUserId(), param.getChannel());
        List<TransactionChannel> channelDataList = parser.parse(mailList);

        channelDataList.stream().filter(TransactionChannel::isSuccess).forEach(this::save);
    }

    private void save(TransactionChannel item) {
        this.transactionChannelLogRepository.batchAdd(item);
        this.transactionLogRepository.batchAdd(item);
    }


    public List<Transaction> analysisLog(AnalysisParamVO param) {
        List<TransactionLog> data = this.transactionLogRepository.list(param.getUserId(), param.getStartTime(), param.getEndTime());
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
                // 时间超过直接break（因为已经排序）
                if (tx.notInRange(log.getDate())) {
                    break;
                }
                if (tx.merge(log)) {
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


    
}
