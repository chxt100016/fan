package com.chxt.domain.tennis;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;

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




    public static Map<String, List<String>> getDayOfWeekAndTime(List<TennisCourt> tennisCourts) {
        if (tennisCourts == null || tennisCourts.isEmpty()) {
            return Map.of();
        }
        // 按照日期分组, 聚合时间
        return tennisCourts.stream()
            .collect(Collectors.groupingBy(
                item -> DateStandardUtils.getDayOfWeekStrCN(item.getDate()),
                Collectors.collectingAndThen(
                    Collectors.mapping(
                        item -> DateStandardUtils.getHourPartStr(item.getDate()),
                        Collectors.toList()
                    ),
                    list -> list.stream().distinct().collect(Collectors.toList())
                )
            ));
            }

}
