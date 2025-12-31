package com.chxt.domain.transaction.component;

import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.entity.TransactionLog;
import com.chxt.domain.transaction.model.exception.TransactionParseException;
import com.chxt.domain.transaction.model.vo.ChannelMail;
import com.chxt.domain.utils.Mail;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * 邮件解析策略管理器
 */
@Slf4j
public class MailParser {

    @Setter
    private String userId;

    @Setter
    private PasswordHelper passwordHelper;

    private final Map<String, MailParserStrategy<?>> parserMap = new HashMap<>();

    public void addParser(MailParserStrategy<?> strategy) {
        this.parserMap.put(strategy.getChannel(), strategy);
    }

    public List<TransactionChannel> parse(List<ChannelMail> channelMails) {
        if (CollectionUtils.isEmpty(channelMails)) {
            return List.of();
        }
        // 处理邮件
        List<TransactionChannel> res = new ArrayList<>();
        for (ChannelMail channelMail : channelMails) {
            try {
                List<Mail> mailList = channelMail.getMails();
                MailParserStrategy<?> strategy = parserMap.get(channelMail.getChannel());
                TransactionChannel transactionChannel = this.handleMails(mailList, strategy);
                res.add(transactionChannel);
            } catch (Exception e) {
                log.error("parse mail error", e);
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
        TransactionChannel channel = new TransactionChannel(strategy.getChannel(), userId);
        for (Mail mail : mails) {
            try {
				List<T> data = strategy.parse(mail, this.passwordHelper);
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
} 