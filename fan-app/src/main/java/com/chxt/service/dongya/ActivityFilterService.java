package com.chxt.service.dongya;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.Participant;
import com.chxt.config.DongyaMonitorConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActivityFilterService {

    @Resource
    private DongyaMonitorConfig config;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<DayOfWeek, String> DAY_OF_WEEK_MAP = Map.of(
        DayOfWeek.MONDAY, "周一",
        DayOfWeek.TUESDAY, "周二",
        DayOfWeek.WEDNESDAY, "周三",
        DayOfWeek.THURSDAY, "周四",
        DayOfWeek.FRIDAY, "周五",
        DayOfWeek.SATURDAY, "周六",
        DayOfWeek.SUNDAY, "周日"
    );

    public boolean isNewMatch(Activity activity) {
        if (activity.getCreatedAt() == null || activity.getCreatedAt().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime createdAt = LocalDateTime.parse(activity.getCreatedAt(), DATE_TIME_FORMATTER);
            LocalDateTime threshold = LocalDateTime.now().minusHours(config.getNewMatch().getTimeWindowHours());

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

    public boolean matchesTimeFilter(Activity activity) {
        if (activity.getBeginTime() == null || activity.getBeginTime().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime beginTime = LocalDateTime.parse(activity.getBeginTime(), DATE_TIME_FORMATTER);
            DayOfWeek dayOfWeek = beginTime.getDayOfWeek();
            LocalTime beginTimeOfDay = beginTime.toLocalTime();

            String dayName = DAY_OF_WEEK_MAP.get(dayOfWeek);
            String minTimeString = config.getTimeFilter().getDaysMap().get(dayName);

            if (minTimeString == null || minTimeString.isEmpty()) {
                log.debug("星期 {} 未配置时间筛选，跳过", dayName);
                return false;
            }

            LocalTime minTime = LocalTime.parse(minTimeString);
            boolean matches = !beginTimeOfDay.isBefore(minTime);

            log.debug("活动 {} 时间筛选: {} (星期: {} - 实际: {} - 最小: {})",
                    activity.getActivityId(), matches, dayName, beginTimeOfDay, minTime);
            return matches;
        } catch (Exception e) {
            log.error("解析活动开始时间失败: activityId={}, beginTime={}",
                    activity.getActivityId(), activity.getBeginTime(), e);
            return false;
        }
    }

    public boolean matchesPlaceFilter(Activity activity) {
        if (activity.getPlacename() == null || activity.getPlacename().isEmpty()) {
            return false;
        }

        List<String> configuredPlaces = config.getPlaceFilter().getPlaces();
        if (configuredPlaces == null || configuredPlaces.isEmpty()) {
            log.debug("未配置地点筛选，跳过");
            return false;
        }

        boolean matches = configuredPlaces.stream()
                .anyMatch(configuredPlace -> activity.getPlacename().contains(configuredPlace));

        log.debug("活动 {} 地点筛选: {} (placename: {})",
                activity.getActivityId(), matches, activity.getPlacename());
        return matches;
    }

    public boolean hasNewFemaleParticipants(Activity activity, ActivityCacheData cachedData) {
        if (cachedData == null || cachedData.getFemaleParticipantIds() == null) {
            log.debug("活动 {} 无缓存数据，无法检测新女生", activity.getActivityId());
            return false;
        }

        if (activity.getParticipants() == null || activity.getParticipants().isEmpty()) {
            return false;
        }

        Set<Integer> currentFemaleIds = activity.getParticipants().stream()
                .filter(p -> p.getGender() != null && p.getGender() == 2)
                .map(Participant::getId)
                .collect(Collectors.toSet());

        Set<Integer> newFemaleIds = new HashSet<>(currentFemaleIds);
        newFemaleIds.removeAll(cachedData.getFemaleParticipantIds());

        boolean hasNew = !newFemaleIds.isEmpty();

        if (hasNew) {
            List<String> newFemaleNames = activity.getParticipants().stream()
                    .filter(p -> newFemaleIds.contains(p.getId()))
                    .map(Participant::getName)
                    .toList();
            log.info("活动 {} 检测到新女生加入: {}", activity.getActivityId(), newFemaleNames);
        }

        return hasNew;
    }

    public boolean shouldMonitor(Activity activity, ActivityCacheData cachedData) {
        boolean matchesTime = matchesTimeFilter(activity);
        boolean matchesPlace = matchesPlaceFilter(activity);

        if (!matchesTime || !matchesPlace) {
            return false;
        }

        boolean isNewMatch = isNewMatch(activity);
        boolean hasNewFemale = hasNewFemaleParticipants(activity, cachedData);

        boolean shouldMonitor = isNewMatch || hasNewFemale;

        log.info("活动 {} 监控决策: {} (新比赛: {}, 新女生: {})",
                activity.getActivityId(), shouldMonitor, isNewMatch, hasNewFemale);

        return shouldMonitor;
    }
}
