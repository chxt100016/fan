package com.chxt.gateway.transaction;

import com.chxt.db.transaction.entity.MessageBoxPO;
import com.chxt.db.transaction.entity.UserMailPO;
import com.chxt.db.transaction.repository.MessageBoxRepository;
import com.chxt.db.transaction.repository.UserMailRepository;
import com.chxt.domain.transaction.component.MailParser;
import com.chxt.domain.transaction.component.MailParserStrategy;
import com.chxt.domain.transaction.component.MailPicker;
import com.chxt.domain.transaction.component.UserMailFactory;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class UserMailFactoryImpl implements UserMailFactory {

    private static final Map<String, MailParserStrategy<?>> ALL_STRATEGIES = TransactionEnums.Channel.getAllParser();

    @Resource
    private UserMailRepository userMailRepository;

    @Resource
    private MessageBoxRepository messageBoxRepository;



    @Override
    public MailPicker getPicker(String userId, String startDate, List<String> channel) {
        UserMailPO userMail = this.userMailRepository.getByUserId(userId);
        if (userMail == null) {
            return  null;
        }

        MailPicker picker = new MailPicker()
                .setUserId(userId)
                .setHost(userMail.getHost())
                .setUsername(userMail.getUsername())
                .setPassword(userMail.getPassword())
                .setStartDateStr(startDate);

        channel.stream().map(ALL_STRATEGIES::get).forEach(picker::addMailCoordinate);
        return picker;

    }

    @Override
    public MailParser getParser(String userId, List<String> channels) {
        MailParser mailParser = new MailParser();
        mailParser.setUserId(userId);
        channels.stream().map(ALL_STRATEGIES::get).forEach(mailParser::addParser);

        // 获取密码
        mailParser.setPasswordHelper((channel, timeStamp, fileName) -> {
            String uniqueNo = userId + ":" + channel + ":" + timeStamp + ":" + fileName;
            MessageBoxPO exist = this.messageBoxRepository.getByUniqueNo(uniqueNo);
            if (exist != null && StringUtils.isNotEmpty(exist.getAnswer())) {
                return exist.getAnswer();
            }
            if (StringUtils.isNotEmpty(channel)) {
                return null;
            }
            MessageBoxPO messageBox = new MessageBoxPO()
                    .setUniqueNo(uniqueNo)
                    .setUserId(userId)
                    .setTitle(TransactionEnums.Channel.getNameByCode(channel))
                    .setMessage(String.format("请输入密码, %s / %s", DateFormatUtils.format(new Date(timeStamp), "yyyy-MM-dd HH:mm:ss"), fileName));
            this.messageBoxRepository.save(messageBox);
            return null;
        });

        return mailParser;
    }
}
