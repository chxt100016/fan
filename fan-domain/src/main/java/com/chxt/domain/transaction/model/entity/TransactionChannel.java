package com.chxt.domain.transaction.model.entity;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChannel {

    @Getter
    private String userId;

    @Getter
    private String date;

    @Getter
    private String channel;

    /**
     * 日期统计
     */
    @Getter
    private Map<String, Integer> dayCountMap = new HashMap<>();

    private List<DateRange> dateRanges;

    private Set<TransactionLog> logs;

	private List<String> errorMessageList;

    public TransactionChannel(String channel, String userId) {
        this.channel = channel;
        this.userId = userId;
    }

    public List<TransactionLog> getLogs() {
        return this.logs == null ? Collections.emptyList() : List.copyOf(this.logs);
    }

    public boolean isSuccess() {
        return CollectionUtils.isNotEmpty(logs);
    }


    public void addLogs(List<TransactionLog> transactionLogs) {
        if (this.logs == null) {
            this.logs = new HashSet<>();
        }
        this.logs.addAll(transactionLogs);

        for (TransactionLog log : transactionLogs) {
            String day = DateFormatUtils.format(log.getDate(), "yyyy-MM-dd");
            this.dayCountMap.compute(day, (k, v) -> v == null ? 1 : v + 1);
        }
    }

	public void addErrorMessage(String message) {
		if (this.errorMessageList == null) {
			this.errorMessageList = new ArrayList<>();
		}
		this.errorMessageList.add(message);
	}

	public void printError() {
		this.errorMessageList.forEach(System.out::println);
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
     */
    public List<String[]> getDateRanges() {
        if (this.dateRanges == null) {
            return Collections.emptyList();
        }
        return this.dateRanges.stream().map(item -> new String[] {item.getStartDateStr(), item.getEndDateStr()}).toList();
    }

    public List<String> getDateStrList() {
        if (MapUtils.isEmpty(dayCountMap)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(dayCountMap.keySet());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class DateRange {
        private Date startDate;
        private String startDateStr;
        private Date endDate;
        private String endDateStr;

        public DateRange(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.startDateStr = DateFormatUtils.format(startDate, "yyyy-MM-dd");
            this.endDateStr = DateFormatUtils.format(endDate, "yyyy-MM-dd");
        }
        

        public boolean merge(DateRange other) {
            if (isOverlap(other)) {
                this.startDate = min(this.startDate, other.getStartDate());
                this.endDate = max(this.endDate, other.getEndDate());
                
                this.startDateStr = DateFormatUtils.format(this.startDate, "yyyy-MM-dd");
                this.endDateStr = DateFormatUtils.format(this.endDate, "yyyy-MM-dd");
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
    public void printLogs() {
        this.logs.stream().sorted(Comparator.comparing(TransactionLog::getDate)).forEach(TransactionLog::printLog);
    }
}
