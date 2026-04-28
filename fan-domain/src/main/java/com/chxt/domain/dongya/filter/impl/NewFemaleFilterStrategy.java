package com.chxt.domain.dongya.filter.impl;


import com.chxt.domain.dongya.filter.ActivityFilterStrategy;
import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.model.ActivityCacheData;
import com.chxt.domain.dongya.model.Participant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 新女生过滤策略
 *
 * <p>检测是否有新女生加入到活动中。
 * 通过比较当前活动的女生参与者和缓存中的女生参与者，识别新加入的女生。</p>
 */
@Slf4j
@Component
public class NewFemaleFilterStrategy implements ActivityFilterStrategy {

    private final ConcurrentHashMap<Integer, ActivityCacheData> activityCache = new ConcurrentHashMap<>();

    @Override
    public boolean test(Activity activity) {
        ActivityCacheData cachedData = activityCache.get(activity.getActivityId());

        if (cachedData == null || cachedData.getFemaleParticipantIds() == null) {
            return false;
        }

        if (activity.getParticipants() == null || activity.getParticipants().isEmpty()) {
            return false;
        }

        Set<Integer> currentFemaleIds = activity.getParticipants().stream()
                .filter(p -> p.getGender() != null && p.getGender() == 2)
                .map(Participant::getId)
                .collect(Collectors.toSet());

        Set<Integer> newFemaleIds = new java.util.HashSet<>(Set.copyOf(currentFemaleIds));
        newFemaleIds.removeAll(cachedData.getFemaleParticipantIds());

        boolean hasNew = !newFemaleIds.isEmpty();

        if (hasNew) {
            List<String> newFemaleNames = activity.getParticipants().stream()
                    .filter(p -> newFemaleIds.contains(p.getId()))
                    .map(Participant::getName)
                    .toList();
            log.info("活动 {} 检测到新女生加入: {}", activity.getActivityId(), newFemaleNames);
        }

        updateCache(activity);
        return hasNew;
    }

    private void updateCache(Activity activity) {
        ActivityCacheData cacheData = ActivityCacheData.of(activity);
        activityCache.put(activity.getActivityId(), cacheData);
        log.debug("更新缓存: activityId={}", activity.getActivityId());
    }
}
