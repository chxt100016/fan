package com.chxt.domain.dongya;


import com.chxt.domain.dongya.filter.FilterManager;
import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.notification.NotificationFormatter;
import com.chxt.domain.notice.NoticeManager;
import com.chxt.domain.notice.model.ScreenEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActivityMonitorService {

    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private NotificationFormatter notificationFormatter;

    @Resource
    private FilterManager filterManager;

    @Resource
    private NoticeManager noticeManager;

    public void monitorActivities() {
        try {
            List<Activity> activities = activityQueryService.queryActivities();
            if (activities.isEmpty()) {
                log.info("未获取到活动数据");
                return;
            }

            for (Activity activity : activities) {
                sendNotification(activity);
            }

        } catch (Exception e) {
            log.error("动呀网球比赛监控任务执行失败", e);
        }
    }

    private void sendNotification(Activity activity) {
        try {
            if (!filterManager.shouldMonitor(activity)) {
                return;
            }

            String message = notificationFormatter.formatNewMatchNotification(activity);
            noticeManager.notice(ScreenEnum.DONG_YA, message);
            log.info("发送通知成功: activityId={}", activity.getActivityId());

        } catch (Exception e) {
            log.error("发送通知失败: activityId={}", activity.getActivityId(), e);
        }
    }

}
