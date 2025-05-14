package com.chxt.domain.pic;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public String getDesc() {
        return this.name + DateFormatUtils.format(this.date, "HH点");
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
