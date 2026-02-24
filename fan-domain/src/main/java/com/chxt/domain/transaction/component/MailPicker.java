package com.chxt.domain.transaction.component;

import com.chxt.domain.transaction.model.constants.TransactionEnums;
import com.chxt.domain.transaction.model.vo.ChannelMail;
import com.chxt.domain.utils.DateRange;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.MailClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 邮件解析策略管理器
 */
@Slf4j
@Data
@Accessors(chain = true)
public class MailPicker {

    private static final Map<String, MailParserStrategy<?>> ALL_STRATEGIES = TransactionEnums.Channel.getAllParser();

    private static final Integer DEFAULT_BATCH_SIZE = 5;

    private static ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_BATCH_SIZE);

    private String userId;

    private String host;

    private String username;

    private String password;

    private String startDateStr;

    private String endDateStr;

    private List<MailCoordinate> mailCoordinates;



    public List<ChannelMail> concurrentSearch() {
        return this.concurrentSearch(false);
    }

    public List<ChannelMail> concurrentSearch(boolean printInfo) {
        if (CollectionUtils.isEmpty(mailCoordinates)) {
            return List.of();
        }

        try {
            // 计算时间区间
            List<DateRange> dateRanges = DateRange.split(this.startDateStr, this.endDateStr, DEFAULT_BATCH_SIZE);

            if (CollectionUtils.isEmpty(dateRanges)) {
                log.warn("split date range failed, use normal search");
                return search(printInfo);
            }

            List<ChannelMail> allChannelMails = new ArrayList<>();

            // 对每个 channel 进行搜索
            for (MailCoordinate coordinate : mailCoordinates) {
                List<Mail> channelAllMails = searchByDateRanges(coordinate, dateRanges, printInfo);

                if (CollectionUtils.isNotEmpty(channelAllMails)) {
                    ChannelMail channelMail = new ChannelMail()
                            .setUserId(userId)
                            .setChannel(coordinate.getChannel())
                            .setMails(channelAllMails);
                    allChannelMails.add(channelMail);
                }
            }

            return allChannelMails;

        } catch (Exception e) {
            log.error("concurrent search mail error: host:{}, username:{}, mailCoordinates:{}, startDate:{}, endDate:{}",
                    this.host, this.username, this.mailCoordinates, this.startDateStr, this.endDateStr, e);
            return List.of();
        }
    }

    /**
     * 按时间区间并发搜索单个渠道的邮件
     */
    private List<Mail> searchByDateRanges(MailCoordinate coordinate, List<DateRange> dateRanges, boolean printInfo) {
        try {
            List<Future<List<Mail>>> futures = new ArrayList<>();
            // 为每个时间区间提交搜索任务
            for (DateRange dateRange : dateRanges) {
                futures.add(executorService.submit(() -> {
                    try (MailClient mailClient = new MailClient(this.host, this.username, this.password, printInfo)) {
                        return mailClient.search(
                                dateRange.getStartDateStr(),
                                dateRange.getEndDateStr(),
                                coordinate.getFrom(),
                                coordinate.getSubject()
                        );
                    } catch (Exception e) {
                        log.error("search mail error for channel:{}, dateRange:[{} - {}]",
                                coordinate.getChannel(), dateRange.getStartDateStr(), dateRange.getEndDateStr(), e);
                        return List.of();
                    }
                }));
            }

            // 收集所有时间区间的搜索结果
            List<Mail> allMails = new ArrayList<>();
            for (Future<List<Mail>> future : futures) {
                try {
                    List<Mail> mails = future.get();
                    if (CollectionUtils.isNotEmpty(mails)) {
                        allMails.addAll(mails);
                    }
                } catch (Exception e) {
                    log.error("get future result error for channel:{}", coordinate.getChannel(), e);
                }
            }

            return allMails;

        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    public List<ChannelMail> search() {
        return search(false);
    }

    /**
     * 查询邮件
     */
    public List<ChannelMail> search(boolean printInfo) {
        try (MailClient mailClient = new MailClient(this.host, this.username, this.password, printInfo)) {
            List<ChannelMail> channelMails = new ArrayList<>();
            for (MailCoordinate coordinate : mailCoordinates) {
                List<Mail> mails = mailClient.search(this.getStartDateStr(), this.getEndDateStr(),  coordinate.getFrom(), coordinate.getSubject());
                if (CollectionUtils.isNotEmpty(mails)) {
                    ChannelMail channelMail = new ChannelMail().setUserId(userId).setChannel(coordinate.getChannel()).setMails(mails);
                    channelMails.add(channelMail);
                }
            }
            return channelMails;
        } catch (Exception e) {
            log.error("search mail error: host:{}, username:{}, mailCoordinates:{}, startDate:{}, endDate:{}", this.host, this.username, this.mailCoordinates, this.startDateStr, this.endDateStr, e);
            return List.of();
        }

    }



    public void addMailCoordinate(MailParserStrategy<?>  mailParserStrategy) {
        if (CollectionUtils.isEmpty(this.mailCoordinates)) {
            this.mailCoordinates = new ArrayList<>();
        }
        this.mailCoordinates.add(new MailCoordinate(mailParserStrategy.getChannel(), mailParserStrategy.getFrom(), mailParserStrategy.getSubject()));
    }

    /**
     * 邮件坐标配置
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class MailCoordinate {

        private String channel;

        private String from;

        private String subject;

    }




} 