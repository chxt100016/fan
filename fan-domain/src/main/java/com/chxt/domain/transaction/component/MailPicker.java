package com.chxt.domain.transaction.component;

import com.alibaba.fastjson.JSON;
import com.chxt.domain.transaction.model.vo.ChannelMail;
import com.chxt.domain.utils.Mail;
import com.chxt.domain.utils.MailClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮件解析策略管理器
 */
@Slf4j
@Data
@Accessors(chain = true)
public class MailPicker {

    private String userId;

    private String host;

    private String username;

    private String password;

    private List<DateRange> dateRanges;

    private List<MailCoordinate>  mailCoordinates;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
                for (DateRange item : dateRanges) {
                    List<Mail> mails = mailClient.search(item.getStartDateStr(), item.getEndDateStr(),  coordinate.getFrom(), coordinate.getSubject());
                    if (CollectionUtils.isNotEmpty(mails)) {
                        ChannelMail channelMail = new ChannelMail().setUserId(userId).setChannel(coordinate.getChannel()).setMails(mails);
                        channelMails.add(channelMail);
                    }
                }
            }
            return channelMails;
        } catch (Exception e) {
            log.error("search mail error: host:{}, username:{}, dateRanges:{}, mailCoordinates:{}", this.host, this.username, JSON.toJSONString(this.dateRanges), this.mailCoordinates, e);
            return List.of();
        }

    }

    /**
     * startDate 到now， 并且排出掉excludeDateStr变为多个时间短
     * @param startDateStr 开始时间
     * @param excludeDateStr 排出的日期
     */
    public void initDateRange(String startDateStr, List<String> excludeDateStr) {
        this.dateRanges = new ArrayList<>();
        LocalDate startLocalDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
        LocalDate now = LocalDate.now();

        if (startLocalDate.isAfter(now)) {
            log.warn("startDate {} is after now, no date range will be generated.", startDateStr);
            return;
        }

        if (CollectionUtils.isEmpty(excludeDateStr)) {
            this.dateRanges.add(new DateRange().setStartDateStr(startDateStr).setEndDateStr(now.format(DATE_FORMATTER)));
            return;
        }

        List<LocalDate> sortedExcludeDates = excludeDateStr.stream()
                .map(date -> LocalDate.parse(date, DATE_FORMATTER))
                .sorted()
                .distinct()
                .toList();

        LocalDate currentStartDate = startLocalDate;

        for (LocalDate excludeDate : sortedExcludeDates) {
            if (excludeDate.isBefore(currentStartDate)) {
                continue;
            }

            if (excludeDate.isAfter(currentStartDate)) {
                LocalDate endDate = excludeDate.minusDays(1);
                this.dateRanges.add(new DateRange()
                        .setStartDateStr(currentStartDate.format(DATE_FORMATTER))
                        .setEndDateStr(endDate.format(DATE_FORMATTER)));
            }
            currentStartDate = excludeDate.plusDays(1);
        }

        if (!currentStartDate.isAfter(now)) {
            this.dateRanges.add(new DateRange()
                    .setStartDateStr(currentStartDate.format(DATE_FORMATTER))
                    .setEndDateStr(now.format(DATE_FORMATTER)));
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

    @Data
    @Accessors(chain = true)
    public static class DateRange {

        private String startDateStr;

        private String endDateStr;
    }

} 