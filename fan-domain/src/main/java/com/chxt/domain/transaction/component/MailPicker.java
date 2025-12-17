package com.chxt.domain.transaction.component;

import com.chxt.domain.transaction.model.vo.ChannelMail;
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

/**
 * 邮件解析策略管理器
 */
@Slf4j
@Data
@Accessors(chain = true)
public class MailPicker {

    private String host;

    private String username;

    private String password;

    private String startDateStr;

    private List<MailCoordinate>  mailCoordinates;


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
                List<Mail> mails = mailClient.search(startDateStr, coordinate.getFrom(), coordinate.getSubject());
                if (CollectionUtils.isNotEmpty(mails)) {
                    channelMails.add(new ChannelMail().setChannel(coordinate.getChannel()).setMails(mails));
                }
            }
            return channelMails;
        } catch (Exception e) {
            log.error("search mail error: host:{}, username:{}, startDateStr:{}, mailCoordinates:{}", this.host, this.username, this.startDateStr, this.mailCoordinates, e);
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