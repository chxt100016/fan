package com.chxt.domain.pic;

import com.chxt.domain.utils.DateStandardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeCell {
    
    private Date date;
    private String key;
    private String name;
    private String iconBase64;

    @SneakyThrows
    public TimeCell(String dateStr, String key) {
        this.key = key;
        this.date = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm");
        

    }




    @SneakyThrows
    public TimeCell(String dateStr, TimetableEnum timetableEnum) {
        this.key = timetableEnum.getCode();
        this.name = timetableEnum.getName();
        this.iconBase64 = timetableEnum.getIconBase64();
        this.date = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm");
    }

    @SneakyThrows
    public TimeCell(Date date, TimetableEnum timetableEnum) {
        this.key = timetableEnum.getCode();
        this.name = timetableEnum.getName();
        this.iconBase64 = timetableEnum.getIconBase64();
        this.date = date;
    }

    public String getUniqueNo() {
        return key + DateStandardUtils.getDayOfWeekStr(date) + DateStandardUtils.getHourOfDay(date);
    }

    public String getDesc() {
        return this.name + DateFormatUtils.format(this.date, "HH点");
    }
    
    /**
     * 合并相同key和date的TimeCell列表
     * @param dataList 原始TimeCell列表
     * @return 合并后的TimeCell列表
     */
    public static List<TimeCell> mergeByKeyAndDate(List<TimeCell> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }
        
        Map<String, TimeCell> mergedMap = new HashMap<>();
        
        for (TimeCell cell : dataList) {
            if (cell.getDate() == null || cell.getKey() == null) {
                continue;
            }
            
            // 使用日期格式化为字符串和key组合作为Map的键
            String mapKey = DateFormatUtils.format(cell.getDate(), "yyyy-MM-dd HH:mm") + "_" + cell.getKey();
            
            // 如果Map中不存在该键，则添加
            if (!mergedMap.containsKey(mapKey)) {
                mergedMap.put(mapKey, cell);
            }
            // 如果需要特殊的合并逻辑，可以在这里添加
            // 例如：合并name字段或选择保留特定的记录等
        }
        
        // 将Map转换回List
        return new ArrayList<>(mergedMap.values());
    }

    public String getDayOfWeek() {
        return DateStandardUtils.getDayOfWeekStr(this.date);


    }

    @SneakyThrows
    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return String.format("%02d:00", hour);
    }

    public static TimeCellIndex index() {
        return new TimeCellIndex();
    }


    public static class TimeCellIndex {

        private String key;

        private String dayOfWeek;

        private Integer hourOfDay;

        public String getUniqueNo() {
            return this.key + this.dayOfWeek + this.hourOfDay;
        }


        public TimeCellIndex key(TimetableEnum key) {
            this.key = key.getCode();
            return this;
        }

        public TimeCellIndex hourOfDay(Integer hourOfDay) {
            this.hourOfDay = hourOfDay;
            return this;
        }

        public TimeCellIndex monday() {
            this.dayOfWeek = "Monday";
            return this;
        }
        public TimeCellIndex tuesday() {
            this.dayOfWeek = "Tuesday";
            return this;
        }

        public TimeCellIndex wednesday() {
            this.dayOfWeek = "Wednesday";
            return this;
        }

        public TimeCellIndex thursday() {
            this.dayOfWeek = "Thursday";
            return this;
        }

        public TimeCellIndex friday() {
            this.dayOfWeek = "Friday";
            return this;
        }

        public TimeCellIndex saturday() {
            this.dayOfWeek = "Saturday";
            return this;
        }

        public TimeCellIndex sunday() {
            this.dayOfWeek = "Sunday";
            return this;
        }
    }



}
