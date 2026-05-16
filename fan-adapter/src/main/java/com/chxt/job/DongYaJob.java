package com.chxt.job;

import com.chxt.domain.dongya.ActivityMonitorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "job.dongya58.enabled", havingValue = "true", matchIfMissing = false)
public class DongYaJob {

    @Resource
    private ActivityMonitorService activityMonitorService;

    @Scheduled(cron = "${job.dongya58.cron:0 0 */2 * * *}")
    public void monitorTennisMatches() {

        activityMonitorService.monitorActivities();
    }
}
