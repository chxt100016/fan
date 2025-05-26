package com.chxt.domain.transaction.parser;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.chxt.domain.transaction.entity.TransactionChannel;
import com.chxt.domain.transaction.entity.TransactionLog;
import com.chxt.domain.transaction.exception.ParseException;
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

    private final MailConfig mailConfig;
    
    private final List<MailParserStrategy<?>> strategies = new ArrayList<>();

    public MailManager(String host, String username, String password, List<MailParserStrategy<?>> strategies) {
        this.mailConfig = new MailConfig(host, username, password);
        this.strategies.addAll(strategies);
    }

    public static Builder Builder() {
        return new Builder();
    }


    public List<TransactionChannel> parse(String startDateStr) {
        return parse(startDateStr, false);
    }
    
    
    /**
     * 使用Mail对象处理所有策略的邮件

     * @param startDateStr 开始`日期
     * @return 所有交易记录列表
     */
    @SneakyThrows
    public List<TransactionChannel> parse(String startDateStr, boolean printInfo) {


        try (MailClient mailClient = new MailClient(this.mailConfig.getHost(), this.mailConfig.getUsername(), this.mailConfig.getPassword(), printInfo)) {
         
         
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
       
            
        } catch (Exception e) {
            System.err.println("处理邮件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>();


      
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
            } catch (ParseException e) {
                this.success = false;
                log.error("{}, subject:{}, from:{}, date:{}", e.getMessage(), mail.getSubject(), mail.getFrom(), mail.getDate(), e);
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
                    .date(strategy.getDate(item))
                    .amount(strategy.getAmount(item))
                    .currency(strategy.getCurrency(item))
                    .type(strategy.getType(item))
                    .method(strategy.getMethod(item))
                    .channel(strategy.getChannel())
                    .description(strategy.getDescription(item))
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

  
    /**
     * 邮件配置
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private class MailConfig {
        
        private String host;

        private String username;

        private String password;
    }

    public static class Builder {


        private String host;


        private String username;


        private String password;

        private List<MailParserStrategy<?>> strategies;

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }
        
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder addStrategy(MailParserStrategy<?> strategy) {
            if (this.strategies == null) {
                this.strategies = new ArrayList<>();
            }
            this.strategies.add(strategy);
            return this;
        }
        

        public MailManager build() {
            return new MailManager(host, username, password, strategies);
        }
    }
    
    
} 