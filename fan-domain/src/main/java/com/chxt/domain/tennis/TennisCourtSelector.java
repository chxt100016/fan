package com.chxt.domain.tennis;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.chxt.domain.pic.TimetableEnum;
import com.chxt.domain.utils.DateStandardUtils;


import lombok.Builder;
import lombok.Data;


@Data

@Builder
public class TennisCourtSelector {


    // 室外场地 工作日
    private static final List<Integer> OUT_DOOR_WEEKDAY_HOURS = Arrays.asList(  20, 21, 22);

    // 室外场地 周末
    private static final List<Integer> OUT_DOOR_WEEKEND_HOURS = Arrays.asList(17, 18, 19, 20, 21, 22);

    // 室内场地 工作日
    private static final List<Integer> IN_DOOR_WEEKDAY_HOURS = Arrays.asList( 20, 21, 22);

    // 室内场地 周末
    private static final List<Integer> IN_DOOR_WEEKEND_HOURS = Arrays.asList(10,11,12,13,14,15,16, 17, 18, 19, 20, 21, 22);


    private static boolean checkLimit(Date date, boolean isIndoor) {
        boolean isWeekend = DateStandardUtils.isWeekend(date);
        Integer hour = DateStandardUtils.getHourOfDay(date);

        if (isWeekend) {
            return isIndoor ? IN_DOOR_WEEKEND_HOURS.contains(hour) : OUT_DOOR_WEEKEND_HOURS.contains(hour);
        } else {
            return isIndoor ? IN_DOOR_WEEKDAY_HOURS.contains(hour) : OUT_DOOR_WEEKDAY_HOURS.contains(hour);
        }
    }

    private static boolean checkLimit(TennisCourt tennisCourt) {
        if (!tennisCourt.getBookable()) {
            return false;
        }
        return checkLimit(tennisCourt.getDate(), tennisCourt.getTimetableEnum().equals(TimetableEnum.HL_INDOOR));
    }

    public static List<TennisCourt> getAvailable(List<TennisCourt> tennisCourts) {
        return tennisCourts.stream().filter(TennisCourtSelector::checkLimit).collect(Collectors.toList());
    }




}
