package com.chxt.domain.dongya;


import com.chxt.domain.dongya.filter.FilterManager;
import com.chxt.domain.dongya.gateway.NotificationGateway;
import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.model.ActivityCacheData;
import com.chxt.domain.dongya.notification.NotificationFormatter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ActivityMonitorService {

    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private NotificationFormatter notificationFormatter;

    @Resource
    private NotificationGateway notificationGateway;

    @Resource
    private FilterManager filterManager;

    private final ConcurrentHashMap<Integer, ActivityCacheData> activityCache = new ConcurrentHashMap<>();

    public void monitorActivities() {
        log.info("开始执行动呀网球比赛监控任务");

        try {
            List<Activity> activities = activityQueryService.queryActivities();

            if (activities.isEmpty()) {
                log.info("未获取到活动数据");
                return;
            }

            log.info("获取到 {} 个活动", activities.size());

            int notificationCount = 0;
            for (Activity activity : activities) {
                try {
                    ActivityCacheData cachedData = activityCache.get(activity.getActivityId());

                    if (shouldMonitor(activity, cachedData)) {
                        sendNotification(activity);
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

    private boolean shouldMonitor(Activity activity, ActivityCacheData cachedData) {
        return filterManager.shouldMonitor(activity, cachedData);
    }

    private void sendNotification(Activity activity) {
        try {

            String message = notificationFormatter.formatNewMatchNotification(activity);

            notificationGateway.sendNotification(message);
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
