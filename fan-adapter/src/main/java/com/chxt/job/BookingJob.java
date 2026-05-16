package com.chxt.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chxt.notice.TennisNoticeService;

import jakarta.annotation.Resource;

@Component
@ConditionalOnProperty(name = "job.booking.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class BookingJob {

    @Resource
    private TennisNoticeService tennisNoticeService;

    @Scheduled(cron = "${job.booking.tennis.cron}") // 每5分钟执行一次
    public void scheduledTouch() {
        log.info("tennis booking");
        tennisNoticeService.touch();
    }
}
