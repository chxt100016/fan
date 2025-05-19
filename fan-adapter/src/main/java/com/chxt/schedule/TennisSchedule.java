package com.chxt.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chxt.notice.TennisNoticeService;

import jakarta.annotation.Resource;

@Component
public class TennisSchedule {

    @Resource
    private TennisNoticeService tennisNoticeService;

    @Scheduled(cron = "0 */5 * * * *") // 每5分钟执行一次
    public void scheduledTouch() {
        tennisNoticeService.touch();
    }
}
