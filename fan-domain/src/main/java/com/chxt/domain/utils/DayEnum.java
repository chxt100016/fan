package com.chxt.domain.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@AllArgsConstructor
@Getter
public enum DayEnum {

    MONDAY("Monday", "一", Calendar.MONDAY),
    TUESDAY("Tuesday", "二", Calendar.TUESDAY),
    WEDNESDAY("Wednesday", "三", Calendar.WEDNESDAY),
    THURSDAY("Thursday", "四", Calendar.THURSDAY),
    FRIDAY("Friday", "五", Calendar.FRIDAY),
    SATURDAY("Saturday", "六", Calendar.SATURDAY),
    SUNDAY("Sunday", "日", Calendar.SUNDAY);

    private final String code;
    private final String name;
    private final Integer calendarIndex;

    /**
     * 根据 Date 获取枚举
     */
    public static DayEnum of(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return getByCalendarIndex(dayOfWeek);
    }

    /**
     * 根据 Calendar 索引获取枚举
     */
    public static DayEnum getByCalendarIndex(int calendarIndex) {
        return Arrays.stream(values())
                .filter(day -> day.getCalendarIndex() == calendarIndex)
                .findFirst()
                .orElse(null);
    }

}