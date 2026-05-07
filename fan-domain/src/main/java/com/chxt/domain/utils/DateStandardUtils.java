package com.chxt.domain.utils;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateStandardUtils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("M/d");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    
    public static final String[] DAY_EN = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

    public static final String[] DAY_CN = { "一", "二", "三", "四", "五", "六", "日" };

    @SneakyThrows
    public static Integer getDayOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int i = cal.get(Calendar.DAY_OF_WEEK);
        return i == Calendar.SUNDAY ? 6 : i - 2;
    }

    @SneakyThrows
    public static String getDayOfWeekStr(Date date){
        int i = getDayOfWeek(date);
        return DAY_EN[i];
    }

    @SneakyThrows
    public static String getDayOfWeekStrCN(Date date){
        int i = getDayOfWeek(date);
        return DAY_CN[i];
    }

    @SneakyThrows
    public static Integer getHourOfDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    @SneakyThrows
    public static String getHourPartStr(Date date){
        return DateFormatUtils.format(date, "HH:mm");
    }


    public static Date buidDate(DayEnum dayOfWeek, Integer hourOfDay) {
        Calendar calendar = Calendar.getInstance();

        // 设置到本周的指定星期几
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek.getCalendarIndex());

        // 设置小时
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    @SneakyThrows
    public static Date addDate(String dateStr, Integer value) {
        Date date = DateUtils.parseDate(dateStr, "yyyy-MM-dd");
        return DateUtils.addDays(date, value);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            log.error("解析时间失败: dateTimeStr={}", dateTimeStr, e);
            return null;
        }
    }

    public static long daysBetween(LocalDateTime from, LocalDateTime to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    public static String formatDateRelative(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "未设置";
        }

        LocalDateTime now = LocalDateTime.now();
        long diffDays = ChronoUnit.DAYS.between(now, dateTime);

        if (diffDays == 0) {
            return "今天";
        } else if (diffDays == 1) {
            return "明天";
        } else if (diffDays == 2) {
            return "后天";
        } else {
            return dateTime.format(MONTH_DAY_FORMATTER);
        }
    }

    public static String formatWeekdayCN(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        return "周" + DAY_CN[dayOfWeek - 1];
    }

    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(TIME_FORMATTER);
    }

    public static String formatTimeInfo(String beginTime, String finishTime) {
        LocalDateTime begin = parseDateTime(beginTime);
        if (begin == null) {
            return "未设置";
        }

        String dateStr = formatDateRelative(begin);
        String weekday = formatWeekdayCN(begin);
        String time = formatTime(begin);

        return String.format("%s(%s) %s", dateStr, weekday, time);
    }
}
