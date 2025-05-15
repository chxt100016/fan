package com.chxt.notice;

import com.chxt.client.huanglong.TennisCourt;
import com.chxt.domain.pic.TimetableEnum;
import com.chxt.domain.utils.DateStandardUtils;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TennisCourtSelector {



    // private final TennisProperty property;


    // public TennisCourtSelector (TennisProperty property) {
    //     this.property = property;
    // }

    


    public List<TennisCourt> getTargetCourt(List<TennisCourt> now, List<TennisCourt> exist) {
        List<TennisCourt> target = new ArrayList<>();
        for (TennisCourt tennisCourt : now) {
            if (!tennisCourt.getBookable()) {
                continue;
            }

            Integer day = DateStandardUtils.getDayOfWeek(tennisCourt.getDate());
            Integer hour = DateStandardUtils.getHourOfDay(tennisCourt.getDate());
            boolean isWeekend = day == 5 || day == 6;
            TimetableEnum timetableEnum = tennisCourt.getTimetableEnum();
            // if (this.property.checkLimit(isWeekend, timetableEnum, hour)) {
                target.add(tennisCourt);
            // }
        }


        
        Map<String, TennisCourt> existMap = exist.stream().collect(
            Collectors.toMap(item -> item.getDate() + item.getTimetableEnum().getCode() + item.getFieldName(), Function.identity())
        );

        boolean hasDiff = false;
        for (TennisCourt item : target) {
            if (existMap.get(item.getDate() + item.getTimetableEnum().getCode() + item.getFieldName()) == null) {
                hasDiff = true;
                break;
            }
        }


        return hasDiff ? target : Collections.emptyList();
    }
}
