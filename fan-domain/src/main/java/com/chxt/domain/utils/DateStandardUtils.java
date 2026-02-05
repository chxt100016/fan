package com.chxt.domain.utils;


import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;

public class DateStandardUtils {

    
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
}
