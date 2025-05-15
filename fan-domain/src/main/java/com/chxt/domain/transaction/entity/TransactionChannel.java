package com.chxt.domain.transaction.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChannel {

    
    private String channel;

    //mutidateRange
    private List<DateRange> dateRanges;

    private List<TransactionLog> transactionLogs;

    public TransactionChannel(String channel) {
        this.channel = channel;
    }


    public void addLogs(List<TransactionLog> transactionLogs) {
        if (this.transactionLogs == null) {
            this.transactionLogs = new ArrayList<>();
        }
        this.transactionLogs.addAll(transactionLogs);
    }

    public void addLog(TransactionLog transactionLog) {
        if (this.transactionLogs == null) {
            this.transactionLogs = new ArrayList<>();
        }
        this.transactionLogs.add(transactionLog);
    }

    public void addDateRange(Date startDate, Date endDate) {
        if (this.dateRanges == null) {
            this.dateRanges = new ArrayList<>();
        }
        this.dateRanges.add(new DateRange(startDate, endDate));

        // 并集
        for (DateRange dateRange : this.dateRanges) {
            if (dateRange.getStartDate().before(startDate) && dateRange.getEndDate().after(startDate)) {
                dateRange.setStartDate(startDate);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class DateRange {
        private Date startDate;
        private Date endDate;
    }
}
