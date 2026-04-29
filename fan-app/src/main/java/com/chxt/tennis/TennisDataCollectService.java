package com.chxt.tennis;

import com.chxt.client.tennistv.TennisTvClient;
import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.tennis.convert.*;
import com.chxt.tennis.convert.DrawMatchAppConvertMapper;
import com.chxt.db.tennis.entity.TennisMatchPO;
import com.chxt.db.tennis.entity.TennisPlayerPO;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.db.tennis.service.TennisMatchService;
import com.chxt.db.tennis.service.TennisPlayerService;
import com.chxt.db.tennis.service.TennisTournamentService;
import com.chxt.tennis.model.Match;
import com.chxt.tennis.model.Player;
import com.chxt.tennis.model.Tournament;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TennisDataCollectService {

    @Resource
    private TennisTvClient tennisTvClient;

    @Resource
    private TennisPlayerService tennisPlayerService;

    @Resource
    private TennisTournamentService tennisTournamentService;

    @Resource
    private TennisMatchService tennisMatchService;

    /**
     * 接口1: 采集进行中的比赛列表
     * 返回有比赛进行中的赛事列表
     */
    public List<Tournament> collectLiveTournaments() {
        log.info("开始采集进行中的比赛列表");

        MatchesResponse response = tennisTvClient.getLiveMatches();
        if (response == null) {
            log.warn("获取最近比赛返回null");
            return List.of();
        }

        // 1. 保存/更新赛事
        List<Tournament> tournaments = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(response.getTournaments())) {
            tournaments = response.getTournaments().stream()
                    .map(TournamentAppConvertMapper.INSTANCE::toTournament)
                    .toList();
            saveTournaments(tournaments);
        }

        // 2. 保存/更新球员（从比赛中提取）
        List<Player> players = extractPlayersFromMatches(response.getMatches());
        savePlayers(players);

        // 3. 保存/更新比赛
        List<Match> matches = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(response.getMatches())) {
            matches = response.getMatches().stream()
                    .map(MatchAppConvertMapper.INSTANCE::toMatch)
                    .toList();
            saveMatches(matches);
        }

        log.info("采集完成: 赛事={}, 球员={}, 比赛={}",
                tournaments.size(), players.size(), matches.size());

        return tournaments;
    }

    /**
     * 接口2: 采集签表数据
     */
    public void collectDraws(String tournamentId, int year) {
        log.info("开始采集签表数据, tournamentId={}, year={}", tournamentId, year);

        DrawsResponse response = tennisTvClient.getDraws(tournamentId, year);
        if (response == null) {
            log.warn("签表数据为空");
            return;
        }

        List<Player> allPlayers = new ArrayList<>();
        List<Match> allMatches = new ArrayList<>();

        // 处理男子单打(MS)
        if (response.getMS() != null && CollectionUtils.isNotEmpty(response.getMS().getRounds())) {
            processDraw(response.getMS(), tournamentId, allPlayers, allMatches);
        }

        savePlayers(allPlayers);
        saveMatches(allMatches);

        log.info("签表采集完成: 球员={}, 比赛={}", allPlayers.size(), allMatches.size());
    }

    private void processDraw(DrawsResponse.Draw draw, String tournamentId,
                             List<Player> allPlayers, List<Match> allMatches) {
        for (DrawsResponse.Round round : draw.getRounds()) {
            if (CollectionUtils.isEmpty(round.getFixtures())) {
                continue;
            }

            for (DrawsResponse.Fixture fixture : round.getFixtures()) {
                // 提取球员
                if (fixture.getResult() != null) {
                    if (fixture.getResult().getTeamTop() != null &&
                            fixture.getResult().getTeamTop().getPlayer() != null) {
                        allPlayers.add(PlayerAppConvertMapper.INSTANCE.toPlayerFromDraw(fixture.getResult().getTeamTop().getPlayer()));
                    }
                    if (fixture.getResult().getTeamBottom() != null &&
                            fixture.getResult().getTeamBottom().getPlayer() != null) {
                        allPlayers.add(PlayerAppConvertMapper.INSTANCE.toPlayerFromDraw(fixture.getResult().getTeamBottom().getPlayer()));
                    }
                }

                // 构建比赛
                Match match = DrawMatchAppConvertMapper.INSTANCE.toMatch(fixture);
                match.setTournamentId(tournamentId);
                match.setDrawType(draw.getEventTypeCode());
                match.setRound(round.getRoundName());
                allMatches.add(match);
            }
        }
    }

    /**
     * 接口3: 采集比赛详情
     */
    public void collectMatchDetails() {
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
                    if (detail.getPlayerTeam1() != null) {
                        allPlayers.add(PlayerAppConvertMapper.INSTANCE.toPlayerFromOop(detail.getPlayerTeam1()));
                    }
                    if (detail.getPlayerTeam2() != null) {
                        allPlayers.add(PlayerAppConvertMapper.INSTANCE.toPlayerFromOop(detail.getPlayerTeam2()));
                    }

                    // 构建比赛
                    Match match = OopMatchAppConvertMapper.INSTANCE.toMatch(detail);
                    allMatches.add(match);
                }
            }
        }

        savePlayers(allPlayers);
        saveMatches(allMatches);

        log.info("比赛详情采集完成: 球员={}, 比赛={}", allPlayers.size(), allMatches.size());
    }

    // ========== 保存方法 ==========

    private void savePlayers(List<Player> players) {
        if (CollectionUtils.isEmpty(players)) {
            return;
        }
        List<TennisPlayerPO> poList = players.stream()
                .map(PlayerAppConvertMapper.INSTANCE::toPlayerPO)
                .toList();
        tennisPlayerService.saveOrUpdateBatch(poList);
    }

    private void saveTournaments(List<Tournament> tournaments) {
        if (CollectionUtils.isEmpty(tournaments)) {
            return;
        }
        List<TennisTournamentPO> poList = tournaments.stream()
                .map(TournamentAppConvertMapper.INSTANCE::toTournamentPO)
                .toList();
        tennisTournamentService.saveOrUpdateBatch(poList);
    }

    private void saveMatches(List<Match> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }
        List<TennisMatchPO> poList = matches.stream()
                .map(MatchAppConvertMapper.INSTANCE::toMatchPO)
                .toList();
        tennisMatchService.saveOrUpdateBatch(poList);
    }

    private List<Player> extractPlayersFromMatches(List<MatchesResponse.MatchInfo> matches) {
        List<Player> players = new ArrayList<>();
        if (matches == null) return players;
        for (MatchesResponse.MatchInfo match : matches) {
            if (match.getPlayerTeam1() != null) players.add(PlayerAppConvertMapper.INSTANCE.toPlayer(match.getPlayerTeam1()));
            if (match.getPlayerTeam2() != null) players.add(PlayerAppConvertMapper.INSTANCE.toPlayer(match.getPlayerTeam2()));
        }
        return players;
    }

}
