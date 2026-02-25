package com.chxt.domain.transaction.component;

import java.util.List;

public interface TransactionEntityRepository {


    MailPicker getPicker(String userId, String startDate, List<String> channel);

    MailParser getParser(String userId, List<String> channel);
}
