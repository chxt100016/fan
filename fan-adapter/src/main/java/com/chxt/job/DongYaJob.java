package com.chxt.job;

import com.chxt.domain.dongya.ActivityMonitorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DongYaJob {

    @Resource
    private ActivityMonitorService activityMonitorService;

    @Scheduled(cron = "${dongya58.monitor.schedule.cron:0 0 */2 * * *}")
    public void monitorTennisMatches() {
        activityMonitorService.monitorActivities();
    }
}
