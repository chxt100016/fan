package com.chxt.domain.tennis;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.chxt.domain.pic.TimetableEnum;
import com.chxt.domain.utils.DateStandardUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TennisCourtSelector {


    private List<Integer> outDoorWeekdayHours;

    private List<Integer> outDoorWeekendHours;

    private List<Integer> inDoorWeekdayHours;

    private List<Integer> intDoorWeekendHours;


    public boolean checkLimit(Date date, boolean isIndoor) {
        boolean isWeekend = DateStandardUtils.isWeekend(date);
        Integer hour = DateStandardUtils.getHourOfDay(date);

        if (isWeekend) {
            return isIndoor ? this.intDoorWeekendHours.contains(hour) : this.outDoorWeekendHours.contains(hour);
        } else {
            return isIndoor ? this.inDoorWeekdayHours.contains(hour) : this.outDoorWeekdayHours.contains(hour);
        }
    }

    public boolean checkLimit(TennisCourt tennisCourt) {
        if (!tennisCourt.getBookable()) {
            return false;
        }
        return this.checkLimit(tennisCourt.getDate(), tennisCourt.getTimetableEnum().equals(TimetableEnum.HL_INDOOR));
    }

    public List<TennisCourt> getAvailable(List<TennisCourt> tennisCourts) {
        return tennisCourts.stream().filter(this::checkLimit).collect(Collectors.toList());
    }




}
