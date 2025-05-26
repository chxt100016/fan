package com.chxt.domain.utils;


import java.util.Calendar;
import java.util.Date;


import lombok.SneakyThrows;

public class DateStandardUtils {

    
    public static final String[] DAY_EN = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };


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
    public static Integer getHourOfDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    @SneakyThrows
    public static String getDayHour(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) + ":" + cal.get(Calendar.HOUR_OF_DAY);
    }

    public static boolean isWeekend(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    


}
