package com.chxt.domain.tennis;


import com.chxt.domain.pic.TimetableEnum;
import com.chxt.domain.utils.DateStandardUtils;
import com.chxt.domain.utils.DayEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Accessors(chain = true)
public class TennisCourt {

    private Date date;
    private Boolean bookable;
    private Integer price;
    private String fieldName;
    private TimetableEnum timetableEnum;

    public String getUniqueNo() {
        return timetableEnum.getCode() + DateStandardUtils.getDayOfWeekStr(date) + DateStandardUtils.getHourOfDay(date);
    }



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

    public static UniqueNoBuilder buildUniqueNo() {
        return new UniqueNoBuilder();
    }


    @Data
    @NoArgsConstructor
    public static class UniqueNoBuilder {

        private List<BuilderConfig> configs = new ArrayList<>();

        // 当前正在构建的配置
        private BuilderConfig currentConfig;

        @Data
        @NoArgsConstructor
        private static class BuilderConfig {
            private DayEnum dayOfWeek;
            private List<TimetableEnum> keys;
            private List<Integer> hours;
        }

        private UniqueNoBuilder initDay(DayEnum dayEnum) {
            currentConfig = new BuilderConfig();
            currentConfig.setDayOfWeek(dayEnum);
            return this;
        }

        public UniqueNoBuilder monday() {
            return initDay(DayEnum.MONDAY);
        }

        public UniqueNoBuilder tuesday() {
            return initDay(DayEnum.TUESDAY);
        }

        public UniqueNoBuilder wednesday() {
            return initDay(DayEnum.WEDNESDAY);
        }

        public UniqueNoBuilder thursday() {
            return initDay(DayEnum.THURSDAY);
        }

        public UniqueNoBuilder friday() {
            return initDay(DayEnum.FRIDAY);
        }

        public UniqueNoBuilder saturday() {
            return initDay(DayEnum.SATURDAY);
        }

        public UniqueNoBuilder sunday() {
            return initDay(DayEnum.SUNDAY);
        }

        public UniqueNoBuilder key(TimetableEnum... data) {
            if (currentConfig == null) {
                throw new IllegalStateException("必须先调用星期方法(如monday())");
            }
            if (ArrayUtils.isEmpty(data)) {
                return this;
            }
            currentConfig.setKeys(Arrays.asList(data));
            return this;
        }

        public UniqueNoBuilder hour(Integer... hours) {
            if (currentConfig == null) {
                throw new IllegalStateException("必须先调用星期方法(如monday())");
            }
            if (CollectionUtils.isEmpty(currentConfig.getKeys())) {
                throw new IllegalStateException("必须先调用key()方法");
            }

            if (ArrayUtils.isEmpty(hours)) {
                return this;

            }
            currentConfig.setHours(Arrays.asList(hours));

            // 完成当前配置，添加到列表中
            configs.add(currentConfig);
            currentConfig = null;
            return this;
        }

        public List<String> getUniqueNo() {
            if (CollectionUtils.isEmpty(configs)) {
                return List.of();
            }

            List<TennisCourt> list = new ArrayList<>();

            for (BuilderConfig config : configs) {
                for (TimetableEnum key : config.getKeys()) {
                    for (Integer hour : config.getHours()) {
                        Date date = DateStandardUtils.buidDate(config.getDayOfWeek(), hour);
                        TennisCourt item = new TennisCourt()
                                .setDate(date)
                                .setTimetableEnum(key);
                        list.add(item);
                    }
                }
            }

            return list.stream()
                    .map(TennisCourt::getUniqueNo)
                    .toList();
        }
    }




}
