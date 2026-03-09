package com.chxt.domain.utils;


import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Slf4j
public class DateRange {

    private Date startDate;

    private Date endDate;

    public DateRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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

    /**
     * 将 yyyy-MM-dd 格式的日期列表转换为多个连续的 DateRange
     */
    @SneakyThrows
    public static List<DateRange> of(List<String> dateList) {
        if (CollectionUtils.isEmpty(dateList)) {
            return List.of();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        // 1. 解析并排序
        List<Date> dates = new ArrayList<>();
        for (String dateStr : dateList) {
            dates.add(sdf.parse(dateStr));
        }
        dates.sort(Date::compareTo);

        // 2. 合并连续日期
        List<DateRange> result = new ArrayList<>();
        Date start = dates.get(0);
        Date prev = start;

        for (int i = 1; i < dates.size(); i++) {
            Date current = dates.get(i);

            // 判断是否连续（间隔一天）
            if (!isNextDay(prev, current)) {
                result.add(new DateRange(start, prev));
                start = current;
            }
            prev = current;
        }

        // 最后一段
        result.add(new DateRange(start, prev));

        return result;
    }

    private static boolean isNextDay(Date d1, Date d2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        cal.add(Calendar.DATE, 1);
        return cal.getTime().equals(d2);
    }

    /**
     * 将时间范围切分为多个区间
     *
     * @param startDateStr 开始日期 格式: yyyy-MM-dd
     * @param endDateStr 结束日期 格式: yyyy-MM-dd
     * @param batchSize 并发数
     * @return 时间区间列表
     */
    public static List<DateRange> split(String startDateStr, String endDateStr, int batchSize) {
        SimpleDateFormat sdf = new SimpleDateFormat();

        try {
            Date startDate = DateUtils.parseDate(startDateStr, "yyyy-MM-dd");
            Date endDate = DateUtils.parseDate(endDateStr, "yyyy-MM-dd");

            // 计算总天数
            long diffInMillis = endDate.getTime() - startDate.getTime();
            long totalDays = diffInMillis / (1000 * 60 * 60 * 24) + 1;

            // 如果总天数小于并发数，则按天数分割
            if (totalDays <= batchSize) {
                batchSize = (int) totalDays;
            }

            List<DateRange> dateRanges = new ArrayList<>();
            long daysPerRange = totalDays / batchSize;
            long remainingDays = totalDays % batchSize;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            for (int i = 0; i < batchSize; i++) {
                Date currentStart = calendar.getTime();

                // 计算当前区间的天数（前面的区间多分配余数）
                long currentRangeDays = daysPerRange + (i < remainingDays ? 1 : 0);

                // 计算当前区间的结束日期
                calendar.add(Calendar.DAY_OF_MONTH, (int) currentRangeDays - 1);
                Date currentEnd = calendar.getTime();

                // 确保最后一个区间的结束日期不超过总结束日期
                if (i == batchSize - 1) {
                    currentEnd = endDate;
                }

                dateRanges.add(new DateRange(currentStart, currentEnd));

                // 移动到下一个区间的起始日期
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            log.info("split date range: total {} days, split into {} ranges", totalDays, dateRanges.size());

            return dateRanges;

        } catch (Exception e) {
            log.error("split date range error: startDate:{}, endDate:{}, concurrentCount:{}",
                    startDateStr, endDateStr, batchSize, e);
            return List.of();
        }
    }



    public static void main(String[] args) {
        List<DateRange> dateRanges = DateRange.of(List.of("2026-02-25", "2026-02-26", "2026-02-22", "2026-02-23"));
        for (DateRange dateRange : dateRanges) {
            System.out.println(dateRange.getStartDateStr() + " -> " + dateRange.getEndDateStr());
        }

//        System.out.println("========== 测试1: 30天分成5个区间 ==========");
//        testSplit("2024-01-01", "2024-01-30", 5);
//
//        System.out.println("\n========== 测试2: 100天分成5个区间 ==========");
//        testSplit("2024-01-01", "2024-04-10", 5);
//
//        System.out.println("\n========== 测试3: 7天分成5个区间（天数小于并发数） ==========");
//        testSplit("2024-01-01", "2024-01-07", 5);
//
//        System.out.println("\n========== 测试4: 365天分成10个区间 ==========");
//        testSplit("2024-01-01", "2024-12-31", 10);
//
//        System.out.println("\n========== 测试5: 3天分成10个区间（天数小于并发数） ==========");
//        testSplit("2024-01-01", "2024-01-03", 10);
//
//        System.out.println("\n========== 测试6: 跨年测试 ==========");
//        testSplit("2023-12-15", "2024-01-15", 5);
//
//        System.out.println("\n========== 测试7: 1天分成5个区间 ==========");
//        testSplit("2024-01-01", "2024-01-01", 5);
    }


    private static void testSplit(String startDateStr, String endDateStr, int concurrentCount) {
        System.out.println("输入参数:");
        System.out.println("  开始日期: " + startDateStr);
        System.out.println("  结束日期: " + endDateStr);
        System.out.println("  并发数: " + concurrentCount);

        List<DateRange> dateRanges = DateRange.split(startDateStr, endDateStr, concurrentCount);

        System.out.println("\n拆分结果:");
        for (int i = 0; i < dateRanges.size(); i++) {
            DateRange range = dateRanges.get(i);
            System.out.println("  区间" + (i + 1) + ": " + range.getStartDateStr() + " ~ " + range.getEndDateStr()
                    + " (共 " + calculateDays(range.getStartDateStr(), range.getEndDateStr()) + " 天)");
        }

        // 验证总天数
        int totalDaysSum = 0;
        for (DateRange range : dateRanges) {
            totalDaysSum += calculateDays(range.getStartDateStr(), range.getEndDateStr());
        }
        System.out.println("\n验证: 所有区间总天数 = " + totalDaysSum);
    }

    private static int calculateDays(String startDateStr, String endDateStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = sdf.parse(startDateStr);
            java.util.Date endDate = sdf.parse(endDateStr);
            long diffInMillis = endDate.getTime() - startDate.getTime();
            return (int) (diffInMillis / (1000 * 60 * 60 * 24)) + 1;
        } catch (Exception e) {
            return 0;
        }
    }

}

