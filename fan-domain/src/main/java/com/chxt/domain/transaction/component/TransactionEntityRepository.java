package com.chxt.domain.transaction.component;

import java.util.List;

public interface TransactionEntityRepository {


    MailPicker getPicker(String userId, String startDate, String endDate, List<String> channel);

    MailParser getParser(String userId, String startDate, String endDate, List<String> channel);
}
