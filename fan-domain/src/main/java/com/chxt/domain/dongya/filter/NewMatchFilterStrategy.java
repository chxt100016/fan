package com.chxt.domain.dongya.filter;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class NewMatchFilterStrategy implements ActivityFilterStrategy {

    private static final int TIME_WINDOW_HOURS = 96;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean test(Activity activity, ActivityCacheData cachedData) {
        if (activity.getCreatedAt() == null || activity.getCreatedAt().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime createdAt = LocalDateTime.parse(activity.getCreatedAt(), DATE_TIME_FORMATTER);
            LocalDateTime threshold = LocalDateTime.now().minusHours(TIME_WINDOW_HOURS);

            boolean isNew = createdAt.isAfter(threshold);
            log.debug("活动 {} 是否为新比赛: {} (createdAt: {}, threshold: {})",
                    activity.getActivityId(), isNew, createdAt, threshold);
            return isNew;
        } catch (Exception e) {
            log.error("解析活动创建时间失败: activityId={}, createdAt={}",
                    activity.getActivityId(), activity.getCreatedAt(), e);
            return false;
        }
    }
}
