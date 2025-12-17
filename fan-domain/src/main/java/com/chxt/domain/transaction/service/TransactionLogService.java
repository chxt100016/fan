package com.chxt.domain.transaction.service;

import com.chxt.domain.transaction.component.MailParser;
import com.chxt.domain.transaction.component.MailPicker;
import com.chxt.domain.transaction.component.UserMailFactory;
import com.chxt.domain.transaction.model.entity.TransactionChannel;
import com.chxt.domain.transaction.model.vo.ChannelMail;
import com.chxt.domain.transaction.model.vo.MailParseParamVO;
import com.chxt.domain.transaction.repository.TransactionChannelLogRepository;
import com.chxt.domain.transaction.repository.TransactionLogRepository;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionLogService {

    @Resource
    private UserMailFactory userMailFactory;

    @Resource
    private TransactionLogRepository transactionLogRepository;

    @Resource
    private TransactionChannelLogRepository transactionChannelLogRepository;

    @SneakyThrows
    public void init(MailParseParamVO param) {
        log.info("开始解析交易记录");

        MailPicker picker = this.userMailFactory.getPicker(param.getUserId(), param.getStartDateStr(), param.getChannel());
        MailParser parser = this.userMailFactory.getParser(param.getUserId(), param.getChannel());

        List<ChannelMail> mailList = picker.search();
        List<TransactionChannel> channelDataList = parser.parse(mailList);
        channelDataList.forEach(this::save);

    }

    private void save(TransactionChannel item) {
        if (CollectionUtils.isEmpty(item.getLogs())) {
            return;
        }
        transactionChannelLogRepository.delByDayChannel(item.getChannel(), item.getDateRanges());
        transactionLogRepository.delByDayChannel(item.getChannel(), item.getDateRanges());
        this.transactionLogRepository.add(item.getLogs());
        this.transactionChannelLogRepository.add(item);

    }



    
}
