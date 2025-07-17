package com.chxt.domain.pic;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.chxt.domain.utils.DateStandardUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

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
    public TimeCell(String dateStr, String key, String iconBase64) {
        this.key = key;
        this.iconBase64 = iconBase64;
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
}
