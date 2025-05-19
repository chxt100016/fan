package com.chxt.client.huanglong.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.chxt.domain.pic.TimetableEnum;
import com.chxt.domain.tennis.TennisCourt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInfoResponse {
    private Integer code;
    private BookInfoData data;

    public List<TennisCourt> toTennisCourt(Date date, TimetableEnum timetableEnum) {
        String dateStr = DateFormatUtils.format(date, "yyyy-MM-dd");
        return data.toTennisCourt(dateStr, timetableEnum);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfoData {
        private List<BookRow> bookingArray;
        private String timeSlot;
        private String fieldSlot;
        private String showLine;

        public List<TennisCourt> toTennisCourt(String dateStr, TimetableEnum timetableEnum) {
            return bookingArray.stream()
                .flatMap(bookRow -> bookRow.toTennisCourt(dateStr, timetableEnum).stream())
                .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookRow {
        private List<BookingInfo> bookingInfos;
        private String _time;

        public List<TennisCourt> toTennisCourt(String dateStr, TimetableEnum timetableEnum) {
            return bookingInfos.stream()
                .flatMap(bookingInfo -> bookingInfo.toTennisCourt(dateStr, timetableEnum).stream())
                .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private String fieldName;
        private String total;
        private String unit;
        private Integer price;
        private String startDate;
        private Integer startDateHours;
        private String startDateMins;
        private String endDate;
        private Integer endDateHours;
        private String endDateMins;
        private String colspan;
        private String rowspan;
        private boolean isRebuild;
        private State state;
        private String orderInfo;
        private String showStartTime;
        private String showEndTime;
        private String showTime;
        private boolean isShowCancel;

        @SneakyThrows
        public List<TennisCourt> toTennisCourt(String dateStr, TimetableEnum timetableEnum) {
            if (this.getStartDateHours() == 20) {
                String aStr = dateStr + " 20:00";
                String bStr = dateStr + " 21:00";
                TennisCourt a = TennisCourt.builder()
                    .date(DateUtils.parseDate(aStr, "yyyy-MM-dd HH:mm"))
                    .bookable(this.getState().getNo() == 0)
                    .price(this.getPrice())
                    .timetableEnum(timetableEnum)
                    .fieldName(this.getFieldName().replaceAll("网球", "").replaceAll("0", "").replaceAll("室内",""))
                    .build();
                TennisCourt b = TennisCourt.builder()
                    .date(DateUtils.parseDate(bStr, "yyyy-MM-dd HH:mm"))
                    .bookable(this.getState().getNo() == 0)
                    .price(this.getPrice())
                    .timetableEnum(timetableEnum)
                    .fieldName(this.getFieldName().replaceAll("网球", "").replaceAll("0", "").replaceAll("室内",""))
                    .build();
                return Arrays.asList(a, b); 
            } else {
                String str = dateStr + " " + this.getShowStartTime();
                TennisCourt tennisCourt = TennisCourt.builder()
                    .date(DateUtils.parseDate(str, "yyyy-MM-dd HH:mm"))
                    .bookable(this.getState().getNo() == 0)
                    .price(this.getPrice())
                    .timetableEnum(timetableEnum)
                    .fieldName(this.getFieldName().replaceAll("网球", "").replaceAll("0", "").replaceAll("室内",""))
                    .build();
                return Collections.singletonList(tennisCourt);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class State{
        private Integer no;
        private String state;
    }
}
