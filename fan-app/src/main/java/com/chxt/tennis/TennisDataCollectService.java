package com.chxt.tennis;

import com.chxt.client.tennistv.TennisTvClient;
import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                    .map(this::convertTournament)
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
                    .map(this::convertMatch)
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
                        allPlayers.add(convertPlayerFromDraw(fixture.getResult().getTeamTop().getPlayer()));
                    }
                    if (fixture.getResult().getTeamBottom() != null &&
                            fixture.getResult().getTeamBottom().getPlayer() != null) {
                        allPlayers.add(convertPlayerFromDraw(fixture.getResult().getTeamBottom().getPlayer()));
                    }
                }

                // 构建比赛
                String playerId1 = null;
                String playerName1 = null;
                String playerId2 = null;
                String playerName2 = null;

                if (fixture.getResult() != null) {
                    if (fixture.getResult().getTeamTop() != null &&
                            fixture.getResult().getTeamTop().getPlayer() != null) {
                        playerId1 = fixture.getResult().getTeamTop().getPlayer().getPlayerId();
                        playerName1 = buildFullName(fixture.getResult().getTeamTop().getPlayer());
                    }
                    if (fixture.getResult().getTeamBottom() != null &&
                            fixture.getResult().getTeamBottom().getPlayer() != null) {
                        playerId2 = fixture.getResult().getTeamBottom().getPlayer().getPlayerId();
                        playerName2 = buildFullName(fixture.getResult().getTeamBottom().getPlayer());
                    }
                }

                Match match = Match.builder()
                        .matchId(fixture.getMatchCode())
                        .tournamentId(tournamentId)
                        .round(round.getRoundName())
                        .drawType(draw.getEventTypeCode())
                        .playerId1(playerId1)
                        .playerId2(playerId2)
                        .playerName1(playerName1)
                        .playerName2(playerName2)
                        .winnerId(fixture.getResult() != null ?
                                String.valueOf(fixture.getResult().getWinner()) : null)
                        .status(convertDrawStatus(fixture.getPulseStatus()))
                        .source("draw")
                        .build();
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
                        allPlayers.add(convertPlayerFromTeam(detail.getPlayerTeam1()));
                    }
                    if (detail.getPlayerTeam2() != null) {
                        allPlayers.add(convertPlayerFromTeam(detail.getPlayerTeam2()));
                    }

                    // 构建比赛
                    Match match = Match.builder()
                            .matchId(detail.getMatchId())
                            .tournamentId(String.valueOf(detail.getTournamentId()))
                            .round(detail.getRound() != null ? detail.getRound().getLongName() : null)
                            .drawType(detail.getAssociationCode())
                            .playerId1(detail.getPlayerTeam1() != null ?
                                    detail.getPlayerTeam1().getPlayerId() : null)
                            .playerId2(detail.getPlayerTeam2() != null ?
                                    detail.getPlayerTeam2().getPlayerId() : null)
                            .playerName1(buildPlayerNameFromTeam(detail.getPlayerTeam1()))
                            .playerName2(buildPlayerNameFromTeam(detail.getPlayerTeam2()))
                            .score(buildScoreString(detail))
                            .status(convertOopStatus(detail.getStatus()))
                            .winnerId(detail.getWinningPlayerId())
                            .courtName(detail.getCourtName())
                            .matchTime(parseDateTime(detail.getMatchDate()))
                            .notBeforeTime(parseDateTime(detail.getNotBeforeISOTime()))
                            .notBeforeText(detail.getNotBeforeText())
                            .source("oop")
                            .build();
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
                .map(this::toPlayerPO)
                .toList();
        tennisPlayerService.saveOrUpdateBatch(poList);
    }

    private void saveTournaments(List<Tournament> tournaments) {
        if (CollectionUtils.isEmpty(tournaments)) {
            return;
        }
        List<TennisTournamentPO> poList = tournaments.stream()
                .map(this::toTournamentPO)
                .toList();
        tennisTournamentService.saveOrUpdateBatch(poList);
    }

    private void saveMatches(List<Match> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }
        List<TennisMatchPO> poList = matches.stream()
                .map(this::toMatchPO)
                .toList();
        tennisMatchService.saveOrUpdateBatch(poList);
    }

    // ========== 转换方法 ==========

    private Tournament convertTournament(MatchesResponse.TournamentInfo info) {
        return Tournament.builder()
                .tournamentId(info.getId())
                .name(info.getName())
                .surface(info.getSurface())
                .category(info.getType())
                .city(null)
                .country(info.getLocation())
                .startDate(parseDate(info.getStart()))
                .endDate(parseDate(info.getEnd()))
                .year(info.getYear())
                .status("active")
                .build();
    }

    private Match convertMatch(MatchesResponse.MatchInfo info) {
        return Match.builder()
                .matchId(info.getId())
                .tournamentId(info.getTournamentId())
                .round(info.getRound())
                .roundName(info.getRoundName())
                .playerId1(info.getPlayer1() != null ? info.getPlayer1().getId() : null)
                .playerId2(info.getPlayer2() != null ? info.getPlayer2().getId() : null)
                .playerName1(info.getPlayer1() != null ? buildFullName(info.getPlayer1()) : null)
                .playerName2(info.getPlayer2() != null ? buildFullName(info.getPlayer2()) : null)
                .score(info.getScore())
                .status(info.getStatus())
                .matchTime(parseDateTime(info.getStartTime()))
                .source("live")
                .build();
    }

    private Player convertPlayer(MatchesResponse.PlayerInfo info) {
        return Player.builder()
                .playerId(info.getId())
                .firstName(info.getFirstName())
                .lastName(info.getLastName())
                .fullName(info.getFullName())
                .nationality(info.getNationality())
                .countryCode(info.getCountryCode())
                .build();
    }

    private Player convertPlayerFromDraw(DrawsResponse.PlayerInfo info) {
        return Player.builder()
                .playerId(info.getPlayerId())
                .firstName(info.getFirstName())
                .lastName(info.getLastName())
                .nationality(info.getNationality())
                .build();
    }

    private Player convertPlayerFromTeam(OopResponse.PlayerTeam team) {
        if (team == null) return null;
        return Player.builder()
                .playerId(team.getPlayerId())
                .firstName(team.getPlayerFirstNameFull() != null ?
                        team.getPlayerFirstNameFull() : team.getPlayerFirstName())
                .lastName(team.getPlayerLastName())
                .nationality(team.getPlayerCountryCode())
                .build();
    }

    private List<Player> extractPlayersFromMatches(List<MatchesResponse.MatchInfo> matches) {
        List<Player> players = new ArrayList<>();
        if (matches == null) return players;
        for (MatchesResponse.MatchInfo match : matches) {
            if (match.getPlayer1() != null) players.add(convertPlayer(match.getPlayer1()));
            if (match.getPlayer2() != null) players.add(convertPlayer(match.getPlayer2()));
        }
        return players;
    }

    private String buildFullName(MatchesResponse.PlayerInfo info) {
        if (info.getFullName() != null) return info.getFullName();
        StringBuilder sb = new StringBuilder();
        if (info.getFirstName() != null) sb.append(info.getFirstName());
        if (info.getLastName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(info.getLastName());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String buildFullName(DrawsResponse.PlayerInfo info) {
        StringBuilder sb = new StringBuilder();
        if (info.getFirstName() != null) sb.append(info.getFirstName());
        if (info.getLastName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(info.getLastName());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String buildPlayerNameFromTeam(OopResponse.PlayerTeam team) {
        if (team == null) return null;
        StringBuilder sb = new StringBuilder();
        if (team.getPlayerFirstNameFull() != null) {
            sb.append(team.getPlayerFirstNameFull());
        } else if (team.getPlayerFirstName() != null) {
            sb.append(team.getPlayerFirstName());
        }
        if (team.getPlayerLastName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(team.getPlayerLastName());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String buildScoreString(OopResponse.MatchDetail detail) {
        if (detail.getPlayerTeam1() == null || detail.getPlayerTeam2() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        List<OopResponse.SetScore> sets1 = detail.getPlayerTeam1().getSets();
        List<OopResponse.SetScore> sets2 = detail.getPlayerTeam2().getSets();
        if (sets1 != null && sets2 != null) {
            for (int i = 0; i < Math.max(sets1.size(), sets2.size()); i++) {
                if (i > 0) sb.append(" ");
                String score1 = i < sets1.size() ? sets1.get(i).getSetScore() : "0";
                String score2 = i < sets2.size() ? sets2.get(i).getSetScore() : "0";
                sb.append(score1).append("-").append(score2);
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String convertDrawStatus(String pulseStatus) {
        if (pulseStatus == null) return "scheduled";
        return switch (pulseStatus) {
            case "C" -> "finished";
            case "L" -> "live";
            case "U" -> "scheduled";
            default -> "scheduled";
        };
    }

    private String convertOopStatus(String status) {
        if (status == null) return null;
        return switch (status) {
            case "F" -> "finished";
            case "L" -> "live";
            case "S" -> "scheduled";
            case "C" -> "cancelled";
            default -> status;
        };
    }

    // ========== PO转换方法 ==========

    private TennisPlayerPO toPlayerPO(Player player) {
        TennisPlayerPO po = new TennisPlayerPO();
        po.setPlayerId(player.getPlayerId());
        po.setFirstName(player.getFirstName());
        po.setLastName(player.getLastName());
        po.setFullName(player.getFullName());
        po.setNationality(player.getNationality());
        po.setCountryCode(player.getCountryCode());
        po.setRank(player.getRank());
        return po;
    }

    private TennisTournamentPO toTournamentPO(Tournament tournament) {
        TennisTournamentPO po = new TennisTournamentPO();
        po.setTournamentId(tournament.getTournamentId());
        po.setName(tournament.getName());
        po.setSurface(tournament.getSurface());
        po.setCategory(tournament.getCategory());
        po.setCity(tournament.getCity());
        po.setCountry(tournament.getCountry());
        po.setStartDate(tournament.getStartDate());
        po.setEndDate(tournament.getEndDate());
        po.setYear(tournament.getYear());
        po.setStatus(tournament.getStatus());
        return po;
    }

    private TennisMatchPO toMatchPO(Match match) {
        TennisMatchPO po = new TennisMatchPO();
        po.setMatchId(match.getMatchId());
        po.setTournamentId(match.getTournamentId());
        po.setRound(match.getRound());
        po.setRoundName(match.getRoundName());
        po.setDrawType(match.getDrawType());
        po.setPlayer1Id(match.getPlayerId1());
        po.setPlayer2Id(match.getPlayerId2());
        po.setPlayer1Name(match.getPlayerName1());
        po.setPlayer2Name(match.getPlayerName2());
        po.setScore(match.getScore());
        po.setSetsScore(match.getSetsScore());
        po.setStatus(match.getStatus());
        po.setWinnerId(match.getWinnerId());
        po.setCourtName(match.getCourtName());
        po.setNotBeforeTime(match.getNotBeforeTime());
        po.setNotBeforeText(match.getNotBeforeText());
        po.setMatchTime(match.getMatchTime());
        po.setSource(match.getSource());
        return po;
    }

    // ========== 日期解析方法 ==========

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            return null;
        }
    }
}
