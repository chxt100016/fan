package com.chxt.domain.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

@Slf4j
public class DateRanges {

    private final List<DateRange> ranges = new ArrayList<>();

    public List<DateRange> getRanges() {
        return Collections.unmodifiableList(ranges);
    }

    public boolean isEmpty() {
        return ranges.isEmpty();
    }

    public void add(String startDate, String endDate) {
        try {
            Date start = DateUtils.parseDate(startDate, "yyyy-MM-dd");
            Date end = DateUtils.parseDate(endDate, "yyyy-MM-dd");
            add(start, end);
        } catch (Exception e) {
            log.error("add date range error: startDate:{}, endDate:{}", startDate, endDate, e);
        }
    }

    private void add(Date startDate, Date endDate) {
        Date start = DateUtils.truncate(startDate, Calendar.DATE);
        Date end = DateUtils.truncate(endDate, Calendar.DATE);

        if (start.after(end)) {
            Date tmp = start;
            start = end;
            end = tmp;
        }

        DateRange waitMergeRange = new DateRange(start, end);
        processMerge(waitMergeRange);
    }

    private void processMerge(DateRange waitMergeRange) {
        boolean merged = false;
        ListIterator<DateRange> iterator = this.ranges.listIterator();
        while (iterator.hasNext()) {
            DateRange item = iterator.next();
            if (item.merge(waitMergeRange)) {
                waitMergeRange = item;
                iterator.remove();
                iterator = this.ranges.listIterator();
            } else if (item.getStartDate().getTime() > waitMergeRange.getEndDate().getTime()) {
                iterator.previous();
                iterator.add(waitMergeRange);
                merged = true;
                break;
            }
        }

        if (!merged) {
            this.ranges.add(waitMergeRange);
        }
    }

    /**
     * 将时间范围切分为多个区间
     *
     * @param startDateStr 开始日期 格式: yyyy-MM-dd
     * @param endDateStr 结束日期 格式: yyyy-MM-dd
     * @param batchSize 并发数
     * @return 时间区间集合
     */
    public static DateRanges split(String startDateStr, String endDateStr, int batchSize) {
        try {
            Date startDate = DateUtils.parseDate(startDateStr, "yyyy-MM-dd");
            Date endDate = DateUtils.parseDate(endDateStr, "yyyy-MM-dd");

            if (startDate.after(endDate) || batchSize <= 0) {
                return new DateRanges();
            }

            long diffInMillis = endDate.getTime() - startDate.getTime();
            long totalDays = diffInMillis / (1000 * 60 * 60 * 24) + 1;

            if (totalDays <= batchSize) {
                batchSize = (int) totalDays;
            }

            DateRanges dateRanges = new DateRanges();
            long daysPerRange = totalDays / batchSize;
            long remainingDays = totalDays % batchSize;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            for (int i = 0; i < batchSize; i++) {
                Date currentStart = calendar.getTime();
                long currentRangeDays = daysPerRange + (i < remainingDays ? 1 : 0);

                calendar.add(Calendar.DAY_OF_MONTH, (int) currentRangeDays - 1);
                Date currentEnd = calendar.getTime();

                if (i == batchSize - 1) {
                    currentEnd = endDate;
                }

                dateRanges.ranges.add(new DateRange(currentStart, currentEnd));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            log.info("split date range: total {} days, split into {} ranges", totalDays, dateRanges.ranges.size());
            return dateRanges;

        } catch (Exception e) {
            log.error("split date range error: startDate:{}, endDate:{}, concurrentCount:{}", startDateStr, endDateStr, batchSize, e);
            return new DateRanges();
        }
    }

    /**
     * 将离散日期列表按连续性分组，合并为多个DateRange区间
     *
     * @param dateStrList 日期字符串列表，格式: yyyy-MM-dd
     * @return 连续日期区间集合
     */
    public static DateRanges of(List<String> dateStrList) {
        if (dateStrList == null || dateStrList.isEmpty()) {
            return new DateRanges();
        }

        try {
            List<Date> dates = new ArrayList<>();
            for (String dateStr : dateStrList) {
                dates.add(DateUtils.parseDate(dateStr, "yyyy-MM-dd"));
            }
            dates.sort(Comparator.naturalOrder());

            DateRanges result = new DateRanges();
            Date rangeStart = dates.get(0);
            Date rangeEnd = dates.get(0);

            for (int i = 1; i < dates.size(); i++) {
                Date current = dates.get(i);
                Date expectedNext = DateUtils.addDays(rangeEnd, 1);
                if (!current.after(expectedNext) && !current.before(expectedNext)) {
                    rangeEnd = current;
                } else if (current.after(rangeEnd)) {
                    result.ranges.add(new DateRange(rangeStart, rangeEnd));
                    rangeStart = current;
                    rangeEnd = current;
                }
            }

            result.ranges.add(new DateRange(rangeStart, rangeEnd));
            log.info("of date list: input {} dates, merged into {} ranges", dateStrList.size(), result.ranges.size());
            return result;

        } catch (Exception e) {
            log.error("of date list error: dateStrList:{}", dateStrList, e);
            return new DateRanges();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        private Date startDate;
        private Date endDate;

        public String show() {
            return getStartDateStr() + " - " + getEndDateStr();
        }

        public String getStartDateStr() {
            if (Objects.isNull(startDate)) {
                return null;
            }
            return DateFormatUtils.format(startDate, "yyyy-MM-dd");
        }

        public String getEndDateStr() {
            if (Objects.isNull(endDate)) {
                return null;
            }
            return DateFormatUtils.format(endDate, "yyyy-MM-dd");
        }

        public boolean merge(DateRange other) {
            if (isOverlap(other)) {
                this.startDate = min(this.startDate, other.getStartDate());
                this.endDate = max(this.endDate, other.getEndDate());
                return true;
            }
            return false;
        }

        private boolean isOverlap(DateRange other) {
            long fixedStart = DateUtils.addDays(this.getStartDate(), -1).getTime();
            long fixedEnd = DateUtils.addDays(this.getEndDate(), 1).getTime();
            return other.getStartDate().getTime() >= fixedStart && other.getStartDate().getTime() <= fixedEnd
                    || other.getEndDate().getTime() >= fixedStart && other.getEndDate().getTime() <= fixedEnd
                    || other.getStartDate().getTime() < fixedStart && other.getEndDate().getTime() > fixedEnd;
        }

        private Date max(Date date1, Date date2) {
            return date1.getTime() > date2.getTime() ? date1 : date2;
        }

        private Date min(Date date1, Date date2) {
            return date1.getTime() < date2.getTime() ? date1 : date2;
        }
    }
}
