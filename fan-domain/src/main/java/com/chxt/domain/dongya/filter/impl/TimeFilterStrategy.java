package com.chxt.domain.dongya.filter.impl;


import com.chxt.domain.dongya.filter.ActivityFilterStrategy;
import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.model.ActivityCacheData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 时间过滤策略
 *
 * <p>根据活动开始时间的星期几来过滤，只保留在指定最小时间之后开始的活动。
 * 不同星期几配置了不同的最小开始时间，用于筛选合适的活动时间。</p>
 */
@Slf4j
@Component
public class TimeFilterStrategy implements ActivityFilterStrategy {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    private static final Map<DayOfWeek, LocalTime> MIN_TIMES = Map.of(
            DayOfWeek.MONDAY, LocalTime.of(9, 0),
            DayOfWeek.TUESDAY, LocalTime.of(10, 0),
            DayOfWeek.WEDNESDAY, LocalTime.of(9, 0),
            DayOfWeek.THURSDAY, LocalTime.of(10, 0),
            DayOfWeek.FRIDAY, LocalTime.of(9, 0),
            DayOfWeek.SATURDAY, LocalTime.of(8, 0),
            DayOfWeek.SUNDAY, LocalTime.of(8, 0)
    );

    @Override
    public boolean test(Activity activity) {
        if (activity.getBeginTime() == null || activity.getBeginTime().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime beginTime = LocalDateTime.parse(activity.getBeginTime(), DATE_TIME_FORMATTER);
            DayOfWeek dayOfWeek = beginTime.getDayOfWeek();
            LocalTime beginTimeOfDay = beginTime.toLocalTime();

            LocalTime minTime = MIN_TIMES.get(dayOfWeek);
            if (minTime == null) {
                log.debug("星期 {} 未配置时间筛选，跳过", dayOfWeek);
                return false;
            }

            boolean matches = !beginTimeOfDay.isBefore(minTime);

            log.debug("活动 {} 时间筛选: {} (星期: {} - 实际: {} - 最小: {})",
                    activity.getActivityId(), matches, dayOfWeek, beginTimeOfDay, minTime);
            return matches;
        } catch (Exception e) {
            log.error("解析活动开始时间失败: activityId={}, beginTime={}",
                    activity.getActivityId(), activity.getBeginTime(), e);
            return false;
        }
    }
}
