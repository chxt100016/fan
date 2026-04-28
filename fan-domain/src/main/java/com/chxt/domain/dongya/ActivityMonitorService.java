package com.chxt.domain.dongya;


import com.chxt.domain.dongya.filter.ActivityFilterStrategy;
import com.chxt.domain.dongya.filter.NewFemaleFilterStrategy;
import com.chxt.domain.dongya.filter.NewMatchFilterStrategy;
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
    private List<ActivityFilterStrategy> filterStrategies;

    @Resource
    private NotificationFormatter notificationFormatter;

    @Resource
    private NotificationGateway notificationGateway;

    @Resource
    private NewMatchFilterStrategy newMatchFilterStrategy;

    @Resource
    private NewFemaleFilterStrategy newFemaleFilterStrategy;

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

    private boolean shouldMonitor(Activity activity, ActivityCacheData cachedData) {
        boolean isNewMatch = newMatchFilterStrategy.test(activity, cachedData);
        boolean hasNewFemale = newFemaleFilterStrategy.test(activity, cachedData);

        boolean shouldMonitor = isNewMatch || hasNewFemale;

        log.info("活动 {} 监控决策: {} (新比赛: {}, 新女生: {})",
                activity.getActivityId(), shouldMonitor, isNewMatch, hasNewFemale);

        return shouldMonitor;
    }

    private void sendNotification(Activity activity, ActivityCacheData cachedData) {
        try {
            String message;

            boolean isNewMatch = newMatchFilterStrategy.test(activity, cachedData);
            boolean hasNewFemale = newFemaleFilterStrategy.test(activity, cachedData);

            if (isNewMatch) {
                message = notificationFormatter.formatNewMatchNotification(activity);
            } else if (hasNewFemale) {
                message = notificationFormatter.formatNewFemaleJoinedNotification(activity, cachedData);
            } else {
                log.warn("活动 {} 既不是新比赛也没有新女生，不应发送通知", activity.getActivityId());
                return;
            }

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
