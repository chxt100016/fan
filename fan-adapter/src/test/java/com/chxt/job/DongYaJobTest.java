package com.chxt.job;

import com.chxt.config.DongyaMonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class DongYaJobTest {

    @Autowired
    private DongYaJob dongYaJob;

    @Autowired
    private DongyaMonitorConfig config;

    @Test
    public void manualTriggerMonitor() throws InterruptedException {
        log.info("===== 手动触发动呀监控任务 =====");
        log.info("调度 Cron: {}", config.getSchedule().getCron());
        log.info("监控地点: {}", config.getPlaceFilter().getPlaces());
        log.info("时间窗口: {} 小时", config.getNewMatch().getTimeWindowHours());
        log.info("通知启用: {}", config.getNotice().getEnabled());

        dongYaJob.monitorTennisMatches();

        log.info("===== 监控任务执行完毕 =====");
    }
}
