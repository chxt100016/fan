package com.chxt.schedule;

import com.chxt.tennis.TennisDataCollectService;
import com.chxt.tennis.model.Tournament;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TennisDataCollectSchedule {

    @Resource
    private TennisDataCollectService tennisDataCollectService;

    /**
     * 接口1: 每2分钟采集进行中的比赛列表
     * 高频 - 发现新赛事
     */
//    @Scheduled(cron = "${tennis.collect.matches.cron:0 */2 * * * *}")
    public void collectLiveMatches() {
        try {
            log.info("定时任务: 采集进行中的比赛列表");
            List<Tournament> tournaments = tennisDataCollectService.collectLiveTournaments();

            // 如果发现有进行中的赛事，触发签表采集
            if (CollectionUtils.isNotEmpty(tournaments)) {
                for (Tournament tournament : tournaments) {
                    try {
                        tennisDataCollectService.collectDraws(
                                tournament.getTournamentId(),
                                tournament.getYear()
                        );
                    } catch (Exception e) {
                        log.error("采集签表失败, tournamentId={}", tournament.getTournamentId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("定时任务: 采集进行中的比赛列表失败", e);
        }
    }

    /**
     * 接口3: 每5分钟采集比赛详情
     * 中频 - 更新比分
     */
//    @Scheduled(cron = "${tennis.collect.oop.cron:0 */5 * * * *}")
    public void collectMatchDetails() {
        try {
            log.info("定时任务: 采集比赛详情");
            tennisDataCollectService.collectMatchDetails();
        } catch (Exception e) {
            log.error("定时任务: 采集比赛详情失败", e);
        }
    }
}
