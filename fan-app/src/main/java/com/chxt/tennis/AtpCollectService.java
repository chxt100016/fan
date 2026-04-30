package com.chxt.tennis;

import com.chxt.client.tennistv.TennisTvClient;
import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.tennis.model.Match;
import com.chxt.tennis.model.Player;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AtpCollectService {

    @Resource
    private TennisTvClient tennisTvClient;

    @Resource
    private AtpTournamentService atpTournamentService;

    @Resource
    private AtpPlayerService atpPlayerService;

    @Resource
    private AtpMatchService atpMatchService;

    /**
     * 采集进行中的比赛列表
     * 返回有比赛进行中的赛事列表
     */
    public void tournaments() {
        MatchesResponse response = tennisTvClient.getLiveMatches();
        if (response == null) {
            log.warn("获取最近比赛返回null");
            return;
        }

        // 1. 保存/更新赛事
        int tSize = atpTournamentService.collect(response.getTournaments());

        // 2. 保存/更新球员（从比赛中提取）
        int pSize = atpPlayerService.collect(response.getMatches());

        // 3. 保存/更新比赛
        int mSize = atpMatchService.collect(response.getMatches());

        log.info("tournaments采集完成: 赛事={}, 球员={}, 比赛={}", tSize, pSize, mSize);
    }

    /**
     * 更新当前比赛签表
     */
    public void currentDraws() {
        List<TennisTournamentPO> tournaments = atpTournamentService.current();

        if (CollectionUtils.isEmpty(tournaments)) {
            log.info("当前无进行中的赛事");
            return;
        }

        for (TennisTournamentPO tournament : tournaments) {
            try {
                this.collectDraws(tournament.getTournamentId(), tournament.getYear());
            } catch (Exception e) {
                log.error("采集签表失败, tournamentId={}", tournament.getTournamentId(), e);
            }
        }
    }




    /**
     * 采集签表数据
     */
    public void collectDraws(String tournamentId, int year) {
        DrawsResponse response = tennisTvClient.getDraws(tournamentId, year);
        if (response == null) {
            log.warn("签表数据为空");
            return;
        }

        List<Player> allPlayers = new ArrayList<>();
        List<Match> allMatches = new ArrayList<>();

        // 处理男子单打(MS)
        if (response.getMS() != null && CollectionUtils.isNotEmpty(response.getMS().getRounds())) {
            // 从签表构建比赛
            allMatches.addAll(atpMatchService.buildFromDraw(response.getMS(), tournamentId));

            // 从签表 fixtures 提取球员
            for (DrawsResponse.Round round : response.getMS().getRounds()) {
                if (CollectionUtils.isEmpty(round.getFixtures())) {
                    continue;
                }
                for (DrawsResponse.Fixture fixture : round.getFixtures()) {
                    allPlayers.addAll(atpPlayerService.extractFromDrawFixture(fixture));
                }
            }
        }

        atpPlayerService.savePlayers(allPlayers);
        atpMatchService.saveMatches(allMatches);

        log.info("签表采集完成: 球员={}, 比赛={}", allPlayers.size(), allMatches.size());
    }

    /**
     * 采集比赛详情
     */
    public void currentMatch() {
        log.info("开始采集比赛详情");

        OopResponse response = tennisTvClient.getOop();
        if (response == null || CollectionUtils.isEmpty(response.getOop())) {
            log.warn("比赛详情数据为空");
            return;
        }

        List<Player> allPlayers = new ArrayList<>();
        List<Match> allMatches = new ArrayList<>();

        for (OopResponse.OopDay oopDay : response.getOop()) {
            if (oopDay.getCourts() == null) {
                continue;
            }

            // 遍历所有场地
            for (Map.Entry<String, OopResponse.CourtDetail> entry : oopDay.getCourts().entrySet()) {
                OopResponse.CourtDetail court = entry.getValue();
                if (court.getMatches() == null) {
                    continue;
                }

                for (OopResponse.MatchDetail detail : court.getMatches()) {
                    // 提取球员
                    allPlayers.addAll(atpPlayerService.extractFromOopMatch(detail));

                    // 构建比赛
                    allMatches.add(com.chxt.tennis.convert.OopMatchAppConvertMapper.INSTANCE.toMatch(detail));
                }
            }
        }

        atpPlayerService.savePlayers(allPlayers);
        atpMatchService.saveMatches(allMatches);

        log.info("比赛详情采集完成: 球员={}, 比赛={}", allPlayers.size(), allMatches.size());
    }


}
