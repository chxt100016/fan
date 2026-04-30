package com.chxt.job;

import com.chxt.tennis.AtpCollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AtpCollectJob {

    @Resource
    private AtpCollectService atpCollectService;

//    @Scheduled(cron = "${tennis.collect.matches.cron}")
    public void collectLiveMatches() {
        try {
            log.info("定时任务: 采集进行中的比赛列表");
            atpCollectService.tournaments();
        } catch (Exception e) {
            log.error("定时任务: 采集进行中的比赛列表失败", e);
        }
    }

//    @Scheduled(cron = "${tennis.collect.draws.cron}")
    public void collectDrawsForCurrentTournaments() {
        try {
            log.info("定时任务: 采集当前赛事签表");
            atpCollectService.draws();
        } catch (Exception e) {
            log.error("定时任务: 采集当前赛事签表失败", e);
        }
    }


//    @Scheduled(cron = "${tennis.collect.oop.cron}")
    public void collectMatchDetails() {
        try {
            log.info("定时任务: 采集进行中比赛详情");
            atpCollectService.currentMatch();
        } catch (Exception e) {
            log.error("定时任务: 采集比赛详情失败", e);
        }
    }
}
