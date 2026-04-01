package com.chxt.db.transaction.repository;

import com.chxt.db.transaction.entity.MessageBoxPO;
import com.chxt.db.transaction.entity.UserMailPO;
import com.chxt.db.transaction.service.MessageBoxRepositoryService;
import com.chxt.db.transaction.service.UserMailRepositoryService;
import com.chxt.domain.transaction.model.entity.RecordParser;
import com.chxt.domain.transaction.component.RecordParserStrategy;
import com.chxt.domain.transaction.model.entity.MailPicker;
import com.chxt.domain.transaction.component.TransactionEntityRepository;
import com.chxt.domain.transaction.model.constants.TransactionEnums;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class TransactionEntityRepositoryImpl implements TransactionEntityRepository {

    private static final Map<String, RecordParserStrategy<?>> ALL_STRATEGIES = TransactionEnums.Channel.getAllParser();

    @Resource
    private UserMailRepositoryService userMailRepositoryService;

    @Resource
    private MessageBoxRepositoryService messageBoxRepositoryService;



    @Override
    public MailPicker getPicker(String userId, String startDate, String endDate, List<String> channel) {
        UserMailPO userMail = this.userMailRepositoryService.getByUserId(userId);
        if (userMail == null) {
            return  null;
        }

        MailPicker picker = new MailPicker()
                .setUserId(userId)
                .setHost(userMail.getHost())
                .setUsername(userMail.getUsername())
                .setPassword(userMail.getPassword())
                .setStartDateStr(startDate)
                .setEndDateStr(endDate);

        channel.stream().map(ALL_STRATEGIES::get).forEach(picker::addMailCoordinate);
        return picker;

    }

    @Override
    public RecordParser getParser(String userId, String startDate, String endDate, List<String> channels) {
        RecordParser recordParser = new RecordParser();
        recordParser.setUserId(userId);
        recordParser.setStartDate(startDate);
        recordParser.setEndDate(endDate != null ? endDate : DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        channels.stream().map(ALL_STRATEGIES::get).forEach(recordParser::addParser);

        // 获取密码
        recordParser.setPasswordHelper((channel, timeStamp, fileName) -> {
            String uniqueNo = userId + ":" + channel + ":" + timeStamp + ":" + fileName;
            MessageBoxPO exist = this.messageBoxRepositoryService.getByUniqueNo(uniqueNo);
            if (exist == null) {
                MessageBoxPO messageBox = new MessageBoxPO()
                        .setUniqueNo(uniqueNo)
                        .setUserId(userId)
                        .setTitle(TransactionEnums.Channel.getNameByCode(channel))
                        .setMessage(String.format("请输入密码, %s / %s", DateFormatUtils.format(new Date(timeStamp), "yyyy-MM-dd HH:mm:ss"), fileName));
                this.messageBoxRepositoryService.save(messageBox);
                return null;
            }

            return StringUtils.isNotEmpty(exist.getAnswer()) ? exist.getAnswer() : null;
        });

        return recordParser;
    }
}
