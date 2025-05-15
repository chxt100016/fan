package com.chxt.domain.transaction.entity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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


    public void addLog(TransactionLog transactionLog) {
        if (this.transactionLogs == null) {
            this.transactionLogs = new ArrayList<>();
        }
        this.transactionLogs.add(transactionLog);
    }

    public void addDateRange(Date startDate, Date endDate) {
        DateRange newRange = new DateRange(startDate, endDate);
        if (this.dateRanges == null) {
            this.dateRanges = new ArrayList<>();  
            this.dateRanges.add(newRange);
        } else {
            boolean merged = false;
            for (DateRange existingRange : this.dateRanges) {
                if (existingRange.merge(newRange)) {
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                this.dateRanges.add(newRange);
            }
        }
        
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class DateRange {
        private Date startDate;
        private Date endDate;

        public boolean merge(DateRange other) {
            if (this.endDate.equals(DateUtils.addDays(other.startDate, -1)) ||
                this.startDate.equals(DateUtils.addDays(other.endDate, 1)) ||
                (this.startDate.before(other.endDate) && this.endDate.after(other.startDate))) {
                this.startDate = this.startDate.before(other.startDate) ? this.startDate : other.startDate;
                this.endDate = this.endDate.after(other.endDate) ? this.endDate : other.endDate;
                return true;
            }
            return false;
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        TransactionChannel channel = new TransactionChannel("aliPay");
        channel.addDateRange(DateUtils.parseDate("2025-05-01", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-02", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-03", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-04", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-05", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-06", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-07", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-08", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-09", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-10", "yyyy-MM-dd"));
        channel.addDateRange(DateUtils.parseDate("2025-05-11", "yyyy-MM-dd"), DateUtils.parseDate("2025-05-12", "yyyy-MM-dd"));
        
        channel.getDateRanges().forEach(range -> {
            System.out.println(DateFormat.getDateInstance().format(range.getStartDate()) + " - " + DateFormat.getDateInstance().format(range.getEndDate()));
        });
    }
}
