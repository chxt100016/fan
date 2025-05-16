package com.chxt.domain.transaction.entity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.time.DateUtils;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChannel {

    @Getter
    private String channel;

    //mutidateRange
    private List<DateRange> dateRanges;

    private List<TransactionLog> transactionLogs;

    public TransactionChannel(String channel) {
        this.channel = channel;
    }


    public void addLog(TransactionLog transactionLog) {
        if (this.transactionLogs == null) {
            this.transactionLogs = new ArrayList<>();
        }
        this.transactionLogs.add(transactionLog);
    }

    public void addDateRange(Date startDate, Date endDate) {
        startDate = DateUtils.truncate(startDate, Calendar.DATE);
        endDate = DateUtils.truncate(endDate, Calendar.DATE);
        
        DateRange waitMergeRange = new DateRange(startDate, endDate);
        if (this.dateRanges == null) {
            this.dateRanges = new ArrayList<>();  
        } 

        processMerge(waitMergeRange);
        
                    
        
    }

    private void processMerge(DateRange waitMergeRange) {
        boolean merged = false;
        ListIterator<DateRange> iterator = this.dateRanges.listIterator();
        while (iterator.hasNext()) {
            DateRange item = iterator.next();
            if (item.merge(waitMergeRange)) {
                waitMergeRange = item;
                iterator.remove();
                iterator = this.dateRanges.listIterator();
            } else {
                if (item.getStartDate().getTime() > waitMergeRange.getEndDate().getTime()) {
                    iterator.previous();
                    iterator.add(waitMergeRange);
                    merged = true;
                    break;
                }
            }
        }
        if (!merged) {
            this.dateRanges.add(waitMergeRange);
        }
    }

    /**
     * 防止被修改
     * @return
     */
    public List<DateRange> getDateRanges() {
        if (this.dateRanges == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.dateRanges);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class DateRange {
        private Date startDate;
        private Date endDate;

        public boolean merge(DateRange other) {
            if (isOverlap(other)) {
                this.startDate = min(this.startDate, other.getStartDate());
                this.endDate = max(this.endDate, other.getEndDate());
                return true;
            }
            
            return false;
        }

        private boolean isOverlap(DateRange other) {
            Long fixedStart = DateUtils.addDays(this.getStartDate(), -1).getTime();
            Long fixedEnd = DateUtils.addDays(this.getEndDate(), 1).getTime();
            return other.getStartDate().getTime() >= fixedStart && other.getStartDate().getTime() <= fixedEnd
                || other.getEndDate().getTime() >= fixedStart && other.getEndDate().getTime() <= fixedEnd
                || other.getStartDate().getTime() < fixedStart && other.getEndDate().getTime() > fixedEnd
            ;
        }

        private Date max(Date date1, Date date2) {
            return date1.getTime() > date2.getTime() ? date1 : date2;
        }

        private Date min(Date date1, Date date2) {
            return date1.getTime() < date2.getTime() ? date1 : date2;
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        TransactionChannel channel = new TransactionChannel("aliPay");
        channel.addDateRange(DateUtils.parseDate("2025-05-07", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-08", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-09", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-10", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-12", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-13", "yyyy-MM-dd"));

        channel.addDateRange(DateUtils.parseDate("2025-05-15", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-15", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-11", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-11", "yyyy-MM-dd"));

        channel.addDateRange(DateUtils.parseDate("2025-05-01", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-01", "yyyy-MM-dd"));
        channel.getDateRanges().forEach(range -> {
            System.out.println(DateFormat.getDateInstance().format(range.getStartDate()) + " - " + DateFormat.getDateInstance().format(range.getEndDate()));
        });
    }
}
