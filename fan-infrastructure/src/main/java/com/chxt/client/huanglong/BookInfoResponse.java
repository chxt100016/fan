package com.chxt.client.huanglong;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInfoResponse {
    private Integer code;
    private BookInfoData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfoData {
        private List<BookRow> bookingArray;
        private String timeSlot;
        private String fieldSlot;
        private String showLine;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookRow {
        private List<BookingInfo> bookingInfos;
        private String _time;
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class State{
        private Integer no;
        private String state;
    }
}
