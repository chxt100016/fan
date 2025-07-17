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
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TennisCourt {

    private Date date;
    private Boolean bookable;
    private Integer price;
    private String fieldName;

    private TimetableEnum timetableEnum;


    public static String getUniqueString(List<TennisCourt> tennisCourts) {
        return tennisCourts.stream()
        .map(
            item -> item.getTimetableEnum().getCode() + ":" + DateStandardUtils.getDayHour(item.getDate()) + ":" + item.getFieldName()
        )
        .collect(Collectors.joining(";"));
    }

}
