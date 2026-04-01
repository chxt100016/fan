package com.chxt.domain.transaction.component;

import com.chxt.domain.transaction.model.entity.MailPicker;
import com.chxt.domain.transaction.model.entity.RecordParser;

import java.util.List;

public interface TransactionEntityRepository {


    MailPicker getPicker(String userId, String startDate, String endDate, List<String> channel);

    RecordParser getParser(String userId, String startDate, String endDate, List<String> channel);
}
