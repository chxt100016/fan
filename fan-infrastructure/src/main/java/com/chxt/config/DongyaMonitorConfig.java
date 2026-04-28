package com.chxt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "dongya58.monitor")
public class DongyaMonitorConfig {

    private ScheduleConfig schedule = new ScheduleConfig();
    private TimeFilterConfig timeFilter = new TimeFilterConfig();
    private PlaceFilterConfig placeFilter = new PlaceFilterConfig();
    private NewMatchConfig newMatch = new NewMatchConfig();
    private NoticeConfig notice = new NoticeConfig();

    @Data
    public static class ScheduleConfig {
        private String cron = "0 0 */2 * * *";
    }

    @Data
    public static class TimeFilterConfig {
        private List<DayConfig> days = new ArrayList<>();

        public Map<String, String> getDaysMap() {
            Map<String, String> map = new HashMap<>();
            if (days != null) {
                for (DayConfig config : days) {
                    map.put(config.getDay(), config.getTime());
                }
            }
            return map;
        }

        @Data
        public static class DayConfig {
            private String day;
            private String time;
        }
    }

    @Data
    public static class PlaceFilterConfig {
        private List<String> places = List.of();
    }

    @Data
    public static class NewMatchConfig {
        private Integer timeWindowHours = 24;
    }

    @Data
    public static class NoticeConfig {
        private Boolean enabled = true;
        private String titlePrefix = "[动呀网球]";
    }
}
