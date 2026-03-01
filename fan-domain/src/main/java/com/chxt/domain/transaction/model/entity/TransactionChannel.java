package com.chxt.domain.transaction.model.entity;


import com.chxt.domain.utils.DateRanges;
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
    private String channel;

    /**
     * 日期统计
     */
    @Getter
    private Map<String, Integer> dayCountMap = new HashMap<>();

    private DateRanges dateRanges;

    private Set<TransactionLog> logs;

	private List<String> errorMessageList;

    public TransactionChannel(String userId, String channel) {
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
            this.logs = new LinkedHashSet<>();
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

        if (this.dateRanges == null) {
            this.dateRanges = new DateRanges();
        }

        this.dateRanges.add(
                DateFormatUtils.format(startDate, "yyyy-MM-dd"),
                DateFormatUtils.format(endDate, "yyyy-MM-dd")
        );
    }

    public List<DateRanges.DateRange> getDateRanges() {
        if (Objects.isNull(this.dateRanges)) {
            return Collections.emptyList();
        }

        return this.dateRanges.getRanges();
    }


    public List<String> getDateStrList() {
        if (MapUtils.isEmpty(dayCountMap)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(dayCountMap.keySet());
    }

}
