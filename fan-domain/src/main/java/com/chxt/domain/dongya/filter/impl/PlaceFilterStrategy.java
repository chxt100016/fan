package com.chxt.domain.dongya.filter.impl;


import com.chxt.domain.dongya.filter.ActivityFilterStrategy;
import com.chxt.domain.dongya.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 地点过滤策略
 *
 * <p>根据活动地点名称进行过滤。如果未配置地点白名单，则通过所有活动。
 * 如果配置了地点白名单，则只保留地点名称包含列表中任意一项的活动。</p>
 */
@Slf4j
@Component
public class PlaceFilterStrategy implements ActivityFilterStrategy {

    private static final List<String> PLACES = List.of();

    @Override
    public boolean test(Activity activity) {
        if (activity.getPlacename() == null || activity.getPlacename().isEmpty()) {
            return false;
        }

        if (PLACES.isEmpty()) {
            log.debug("未配置地点筛选，全部通过");
            return true;
        }

        boolean matches = PLACES.stream()
                .anyMatch(configuredPlace -> activity.getPlacename().contains(configuredPlace));

        log.debug("活动 {} 地点筛选: {} (placename: {})",
                activity.getActivityId(), matches, activity.getPlacename());
        return matches;
    }
}
