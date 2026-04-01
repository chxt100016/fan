package com.chxt.domain.transaction.model.entity;

import com.chxt.domain.transaction.component.PasswordHelper;
import com.chxt.domain.transaction.component.RecordParserContext;
import com.chxt.domain.transaction.component.RecordParserStrategy;
import com.chxt.domain.transaction.model.exception.TransactionParseException;
import com.chxt.domain.transaction.model.vo.ChannelMailVO;
import com.chxt.domain.utils.Mail;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * 邮件解析策略管理器
 */
@Slf4j
public class RecordParser {

    @Setter
    private String userId;

    @Setter
    private String startDate;

    @Setter
    private String endDate;

    @Setter
    private PasswordHelper passwordHelper;

    private final Map<String, RecordParserStrategy<?>> parserMap = new HashMap<>();

    public void addParser(RecordParserStrategy<?> strategy) {
        this.parserMap.put(strategy.getChannel(), strategy);
    }

    public List<TransactionChannel> parse(List<ChannelMailVO> channelMails) {
        if (CollectionUtils.isEmpty(channelMails)) {
            return List.of();
        }
        // 处理邮件
        List<TransactionChannel> res = new ArrayList<>();
        for (ChannelMailVO channelMail : channelMails) {
            try {
                RecordParserStrategy<?> strategy = parserMap.get(channelMail.getChannel());
                TransactionChannel transactionChannel = this.handleMails(channelMail, strategy);
                res.add(transactionChannel);
            } catch (Exception e) {
                log.error("parse mail error", e);
            }
        }
        return res;
    }

    /**
     * 处理邮件
     * @param channelMail 邮件列表
     * @param strategy 策略
     * @return 所有交易记录列表
     */
    private <T> TransactionChannel handleMails(ChannelMailVO channelMail, RecordParserStrategy<T> strategy) {
        String userId = channelMail.getUserId();
        List<Mail> mails = channelMail.getMails();
        TransactionChannel channel = new TransactionChannel(userId, strategy.getChannel());
        for (Mail mail : mails) {
            try {
                RecordParserContext<T> context = new RecordParserContext<>(strategy.getChannel(), mail, this.passwordHelper);
                strategy.parse(context);
                if (CollectionUtils.isEmpty(context.getData())) {
                    continue;
                }
                context.setStartDate(this.startDate);
                context.setEndDate(this.endDate);
				Date startDate = strategy.getTransactionStartDate(context);
				Date endDate = strategy.getTransactionEndDate(context);
				List<TransactionLog> logs = parseMailData(context.getData(), strategy);
				channel.addDateRange(startDate, endDate);
				channel.addLogs(logs);
			} catch (TransactionParseException e) {
				channel.addErrorMessage(e.getMessage());
			} catch (Exception e) {
				log.error("handleMails error, strategy: {}", strategy.getChannel(), e);
			}
        }
        log.info("transaction mail parse, channel:{}, userId:{}, size:{}", channelMail.getChannel(), channelMail.getUserId(), channel.getLogs().size());
        return channel;
    }

	private <T> List<TransactionLog> parseMailData(List<T> data, RecordParserStrategy<T> strategy) {
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
                    .userId(this.userId)
                    .counterparty(strategy.getCounterparty(item))
				    .build();
			logs.add(log);
		}

		return logs;
	}
} 