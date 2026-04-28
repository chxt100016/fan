package com.chxt.domain.dongya.filter.impl;



import com.chxt.domain.dongya.filter.ActivityFilterStrategy;
import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.model.ActivityCacheData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 新比赛过滤策略
 *
 * <p>判断活动是否在指定时间窗口内创建（默认96小时）。
 * 用于识别最近创建的新比赛，及时通知用户。</p>
 */
@Slf4j
@Component
public class NewMatchFilterStrategy implements ActivityFilterStrategy {

    private static final int TIME_WINDOW_HOURS = 2;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    @Override
    public boolean test(Activity activity) {
        if (activity.getCreatedAt() == null || activity.getCreatedAt().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime createdAt = LocalDateTime.parse(activity.getCreatedAt(), DATE_TIME_FORMATTER);
            LocalDateTime threshold = LocalDateTime.now(ZONE_ID).minusHours(TIME_WINDOW_HOURS);

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
