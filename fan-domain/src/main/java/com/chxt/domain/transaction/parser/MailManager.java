package com.chxt.domain.transaction.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections4.CollectionUtils;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.model.exception.TransactionParseException;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.MailClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;

/**
 * 邮件解析策略管理器
 */
@Slf4j
public class MailManager {

	private static final Map<String, MailParserStrategy<?>> ALL_STRATEGIES = TransactionEnums.CHANNEL.getAllParser();

    private final MailConfig mailConfig;
    
    private final List<MailParserStrategy<?>> strategies = new ArrayList<>();

    public MailManager(String host, String username, String password, List<MailParserStrategy<?>> strategies, PasswordHelper passwordHelper) {
        this.mailConfig = new MailConfig(host, username, password, passwordHelper);
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
				res.add(handleMails);
          
            }
            return res;
        } catch (Exception e) {
            log.error("处理邮件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
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
            try {
				List<T> data = strategy.parse(mail, this.mailConfig.getPasswordHelper());
				Date startDate = strategy.getTransactionStartDate(mail, data);
				Date endDate = strategy.getTransactionEndDate(mail, data);
				List<TransactionLog> logs = parseMailData(data, strategy);
				channel.addDateRange(startDate, endDate);
				channel.addLogs(logs);
			} catch (TransactionParseException e) {
				channel.addErrorMessage(e.getMessage());
			} catch (Exception e) {
				log.error("{}", e.getMessage());
			}
        }
        
        return channel;
    }

	private <T> List<TransactionLog> parseMailData(List<T> data, MailParserStrategy<T> strategy) {
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

		return logs;
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

		private PasswordHelper passwordHelper;
    }

    public static class Builder {


        private String host;


        private String username;


        private String password;

        private List<MailParserStrategy<?>> strategies;

		private PasswordHelper passwordHelper;

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

        public Builder addStrategy(String code) {
            if (this.strategies == null) {
                this.strategies = new ArrayList<>();
            }
			MailParserStrategy<?> parser = ALL_STRATEGIES.get(code);
			if (parser == null) {
				log.warn("no this parser, code:{}", code);
				return this;
			}
            this.strategies.add(parser);
            return this;
        }

		public Builder addStrategy(List<String> codeList) {
			codeList.forEach(this::addStrategy);
            return this;
        }

		public Builder setPasswordHelper(PasswordHelper passwordHelper) {
            if (this.strategies == null) {
                this.strategies = new ArrayList<>();
            }
            this.passwordHelper = passwordHelper;
            return this;
        }
        

        public MailManager build() {
            return new MailManager(host, username, password, strategies, passwordHelper);
        }
    }
    
    
} 