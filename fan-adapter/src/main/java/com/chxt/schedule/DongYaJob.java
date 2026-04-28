package com.chxt.schedule;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.bluebubbles.BlueBubblesClient;
import com.chxt.client.dongya58.Dongya58Client;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.ActivityRequest;
import com.chxt.client.dongya58.model.ActivityResponse;
import com.chxt.config.DongyaMonitorConfig;
import com.chxt.service.dongya.ActivityFilterService;
import com.chxt.service.dongya.DongyaNotificationFormatter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DongYaJob {

    @Resource
    private Dongya58Client dongya58Client;

    @Resource
    private BlueBubblesClient blueBubblesClient;

    @Resource
    private DongyaMonitorConfig config;

    @Resource
    private ActivityFilterService filterService;

    @Resource
    private DongyaNotificationFormatter notificationFormatter;

    private final ConcurrentHashMap<Integer, ActivityCacheData> activityCache = new ConcurrentHashMap<>();

    @Scheduled(cron = "${dongya58.monitor.schedule.cron:0 0 */2 * * *}")
    public void monitorTennisMatches() {
        log.info("开始执行动呀网球比赛监控任务");

        try {
            List<Activity> activities = fetchActivities();

            if (activities == null || activities.isEmpty()) {
                log.info("未获取到活动数据");
                return;
            }

            log.info("获取到 {} 个活动", activities.size());

            int notificationCount = 0;
            for (Activity activity : activities) {
                try {
                    ActivityCacheData cachedData = activityCache.get(activity.getActivityId());

                    if (filterService.shouldMonitor(activity, cachedData)) {
                        sendNotification(activity, cachedData);
                        notificationCount++;
                    }
                    updateCache(activity);
                } catch (Exception e) {
                    log.error("处理活动失败: activityId={}", activity.getActivityId(), e);
                }
            }

            log.info("动呀网球比赛监控任务完成，发送通知: {} 条", notificationCount);

        } catch (Exception e) {
            log.error("动呀网球比赛监控任务执行失败", e);
        }
    }

    private List<Activity> fetchActivities() {
        try {
            ActivityRequest request = ActivityRequest.builder()
                    .page(1)
                    .limit(100)
                    .filterSportType(0)
                    .filterMinMaxLevel("4.5,4.5")
                    .filterType(4)
                    .filterDivisionFormat(1)
                    .city("杭州")
                    .build();

            ActivityResponse response = dongya58Client.getActivities(request);

            if (response == null || response.getData() == null) {
                return new ArrayList<>();
            }

            return response.getData();
        } catch (Exception e) {
            log.error("获取活动数据失败", e);
            return new ArrayList<>();
        }
    }

    private void sendNotification(Activity activity, ActivityCacheData cachedData) {
        if (!config.getNotice().getEnabled()) {
            log.info("通知功能未启用，跳过发送");
            return;
        }

        try {
            String message;

            boolean isNewMatch = filterService.isNewMatch(activity);
            boolean hasNewFemale = filterService.hasNewFemaleParticipants(activity, cachedData);

            if (isNewMatch) {
                message = notificationFormatter.formatNewMatchNotification(activity);
            } else if (hasNewFemale) {
                message = notificationFormatter.formatNewFemaleJoinedNotification(activity, cachedData);
            } else {
                log.warn("活动 {} 既不是新比赛也没有新女生，不应发送通知", activity.getActivityId());
                return;
            }

            blueBubblesClient.send(message);
            log.info("发送通知成功: activityId={}", activity.getActivityId());

        } catch (Exception e) {
            log.error("发送通知失败: activityId={}", activity.getActivityId(), e);
        }
    }

    private void updateCache(Activity activity) {
        ActivityCacheData cacheData = ActivityCacheData.of(activity);
        activityCache.put(activity.getActivityId(), cacheData);
        log.debug("更新缓存: activityId={}", activity.getActivityId());
    }
}
