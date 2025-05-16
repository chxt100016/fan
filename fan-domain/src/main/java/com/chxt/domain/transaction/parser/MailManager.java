package com.chxt.domain.transaction.parser;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;


import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.chxt.domain.transaction.entity.TransactionChannel;
import com.chxt.domain.transaction.entity.TransactionLog;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.MailClient;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件解析策略管理器
 */
@Slf4j
public class MailManager {
    
    private final List<MailParserStrategy<?>> strategies = new ArrayList<>();
    
    /**
     * 添加邮件解析策略
     * @param strategy 解析策略
     * @return 当前管理器实例
     */
    public MailManager addStrategy(MailParserStrategy<?> strategy) {
        strategies.add(strategy);
        return this;
    }
    
    
    
    /**
     * 使用Mail对象处理所有策略的邮件

     * @param startDateStr 开始`日期
     * @return 所有交易记录列表
     */
    @SneakyThrows
    public List<TransactionChannel> parse(MailClient mailClient, String startDateStr) {
        List<TransactionChannel> res = new ArrayList<>();
        
        // 一次性获取所有策略的邮件
        List<List<Mail>> allStrategyMails = searchAll(mailClient, startDateStr);
        
       
        // 处理邮件
        for (int i = 0; i < strategies.size(); i++) {
            MailParserStrategy<?> strategy = strategies.get(i);
            List<Mail> mails = allStrategyMails.get(i);

            TransactionChannel handleMails = this.handleMails(mails, strategy);
            if (handleMails != null && !CollectionUtils.isEmpty(handleMails.getLogs())) {
                res.add(handleMails);
            }
        }
        
        return res;
    }

    /**
     * 处理邮件
     * @param mails 邮件列表
     * @param strategy 策略
     * @return 所有交易记录列表
     */
    private <T> TransactionChannel handleMails(List<Mail> mails, MailParserStrategy<T> strategy) {
        if (CollectionUtils.isEmpty(mails)) {
            return null;
        }

        TransactionChannel channel = new TransactionChannel(strategy.getChannel());

        for (Mail mail : mails) {
            MailParser<T> parser = new MailParser<>(strategy, mail);
            if (!parser.canGetData()) {
                continue;
            }
            Date startDate = parser.getStartDate();
            Date endDate = parser.getEndDate();
            channel.addDateRange(startDate, endDate);
            channel.addLogs(parser.getLogs());
        }
        
        return channel;
    }

    /**
     * 获取所有策略需要得邮件
     * @param mailClient
     * @param startDateStr
     * @return
     */
    private List<List<Mail>> searchAll(MailClient mailClient, String startDateStr) {
        List<List<Mail>> allStrategyMails = new ArrayList<>();
        for (MailParserStrategy<?> strategy : strategies) {
            List<Mail> messages = mailClient.search(startDateStr, strategy.getFrom(), strategy.getSubject());
            allStrategyMails.add(messages);
        }
        return allStrategyMails;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class MailParser<T> {

        private List<TransactionLog> logs;

        private Date startDate;

        private Date endDate;

        private boolean success;

        public MailParser(MailParserStrategy<T> strategy, Mail mail) {
            try {
                process(strategy, mail);
                this.success = true;
            } catch (Exception e) {
                this.success = false;
                log.error("transaction mail parse error, subject:{}, from:{}, date:{}, strategy: {}", mail.getSubject(), mail.getFrom(), mail.getDate(), JSON.toJSONString(strategy), e);
            }
        }

        public void process(MailParserStrategy<T> strategy, Mail mail) {
            List<T> data = strategy.parse(mail);
            this.startDate = strategy.getTransactionStartDate(mail, data);
            this.endDate = strategy.getTransactionEndDate(mail, data);

            List<TransactionLog> logs = new ArrayList<>();
            for (T item : data) {
                TransactionLog log = TransactionLog.builder()
                    .dateTime(strategy.getDateTime(item))
                    .amount(strategy.getAmount(item))
                    .currency(strategy.getCurrency(item))
                    .type(strategy.getType(item))
                    .method(strategy.getMethod(item))
                    .channel(strategy.getChannel())
                    .desc(strategy.getDesc(item))
                    .logId(strategy.getLogId(item))
                    .build();
                logs.add(log);
            }
            this.logs = logs;
        }

        public boolean canGetData() {
            return success && !CollectionUtils.isEmpty(logs) && startDate != null && endDate != null;
        }
    }

  

    
} 