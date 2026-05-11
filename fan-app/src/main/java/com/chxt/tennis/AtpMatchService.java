package com.chxt.tennis;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.db.tennis.entity.TennisMatchPO;
import com.chxt.domain.tennis.model.TennisRoundEnum;
import com.chxt.db.tennis.entity.TennisSetScorePO;
import com.chxt.db.tennis.service.TennisMatchService;
import com.chxt.db.tennis.service.TennisSetScoreService;
import com.chxt.tennis.convert.DrawMatchAppConvertMapper;
import com.chxt.tennis.convert.MatchAppConvertMapper;
import com.chxt.tennis.model.Match;
import com.chxt.tennis.model.MatchStatus;
import com.chxt.tennis.model.SetScore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AtpMatchService {

    @Resource
    private TennisMatchService tennisMatchService;

    @Resource
    private TennisSetScoreService tennisSetScoreService;

    public int collect(List<MatchesResponse.MatchInfo> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return 0;
        }
        List<Match> data = matches.stream()
                .map(MatchAppConvertMapper.INSTANCE::toMatch)
                .toList();
        this.saveMatches(data);
        return data.size();
    }

    public List<Match> buildFromDraw(DrawsResponse.Draw draw, String tournamentId, Long drawId, Integer year) {
        List<Match> allMatches = new ArrayList<>();
        if (draw == null || CollectionUtils.isEmpty(draw.getRounds())) {
            return allMatches;
        }

        for (DrawsResponse.Round round : draw.getRounds()) {
            if (CollectionUtils.isEmpty(round.getFixtures())) {
                continue;
            }
            for (DrawsResponse.Fixture fixture : round.getFixtures()) {
                Match match = DrawMatchAppConvertMapper.INSTANCE.toMatch(fixture);
                match.setTournamentId(tournamentId);
                match.setDrawId(drawId);
                match.setYear(year);
                match.setRoundNumber(round.getRoundId());
                match.setRoundName(TennisRoundEnum.toShortName(round.getRoundName()));

                // 如果没有有效的 matchId，生成一个唯一 ID
                if (match.getMatchId() == null || match.getMatchId().isEmpty()) {
                    String generatedId = generateMatchId(tournamentId, drawId, round.getRoundId(),
                            match.getPlayer1Id(), match.getPlayer2Id());
                    match.setMatchId(generatedId);
                }

                allMatches.add(match);
            }
        }
        return allMatches;
    }

    public void saveMatches(List<Match> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }
        List<TennisMatchPO> matchPOs = MatchAppConvertMapper.INSTANCE.toMatchPOList(matches);
        tennisMatchService.saveOrUpdateBatch(matchPOs);

        // 保存 SetScore 数据
        saveSetScores(matches);
    }

    /**
     * 更新进行中的比赛：时长、状态、盘分、场地、球员
     */
    public void updateLiveMatches(List<MatchesResponse.MatchInfo> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }

        List<Match> allMatches = new ArrayList<>();
        List<TennisSetScorePO> allSetScores = new ArrayList<>();

        for (MatchesResponse.MatchInfo info : matches) {
            Match match = new Match();
            match.setMatchId(info.getMatchId());
            match.setTournamentId(info.getTournamentId() != null ? String.valueOf(info.getTournamentId()) : null);
            match.setYear(info.getTournamentYear());
            match.setPlayer1Id(info.getPlayerTeam1() != null ? info.getPlayerTeam1().getPlayerId() : null);
            match.setPlayer2Id(info.getPlayerTeam2() != null ? info.getPlayerTeam2().getPlayerId() : null);
            match.setStatus(MatchStatus.toStatus(info.getStatus()));
            match.setCourt(info.getCourtName());
            match.setDurationMinutes(parseDuration(info.getMatchTime()));
            match.setCourtSeq(info.getCourtSeq());
            allMatches.add(match);

            // 提取盘分：合并两个 PlayerTeam 同一 SetNumber 的数据
            allSetScores.addAll(buildSetScores(info, match.getTournamentId(), match.getYear()));
        }

        // 更新比赛记录
        List<TennisMatchPO> matchPOs = MatchAppConvertMapper.INSTANCE.toMatchPOList(allMatches);
        List<TennisMatchPO> toUpdate = fillIdForUpdate(matchPOs);
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            tennisMatchService.updateBatchById(toUpdate);
        }

        // 更新盘分
        if (CollectionUtils.isNotEmpty(allSetScores)) {
            tennisSetScoreService.saveOrUpdateBatch(allSetScores);
        }

        log.info("进行中比赛更新完成: 比赛={}, 盘分={}", allMatches.size(), allSetScores.size());
    }

    public void updateMatches(List<Match> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }
        List<TennisMatchPO> matchPOs = MatchAppConvertMapper.INSTANCE.toMatchPOList(matches);
        List<TennisMatchPO> toUpdate = fillIdForUpdate(matchPOs);
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            tennisMatchService.updateBatchById(toUpdate);
            log.info("更新已有比赛: {}条", toUpdate.size());
        }
    }

    /**
     * 按业务唯一键 (matchId, tournamentId, year) 查出已存在记录，
     * 把数据库主键 id 回填到待更新的 PO 上。
     * updateBatchById 走的是 @TableId 标注的 id 字段，不回填 id 时 SQL 不会命中任何行。
     */
    private List<TennisMatchPO> fillIdForUpdate(List<TennisMatchPO> matchPOs) {
        // 收集所有非空的 matchId 作为查询条件，先用 matchId 粗筛缩小结果集
        List<String> matchIds = matchPOs.stream()
                .map(TennisMatchPO::getMatchId).filter(Objects::nonNull).distinct().toList();
        if (CollectionUtils.isEmpty(matchIds)) {
            return List.of();
        }
        // matchId 在不同赛事/年份下会重复，必须用 (matchId, tournamentId, year) 三元组作为唯一键
        Map<String, Long> keyToId = tennisMatchService.lambdaQuery()
                .in(TennisMatchPO::getMatchId, matchIds)
                .list().stream()
                .collect(Collectors.toMap(
                        AtpMatchService::uniqueKey,
                        TennisMatchPO::getId,
                        (a, b) -> a));

        // 仅保留库里已存在的记录并回填 id，否则 updateBatchById 因 id=null 而失效
        return matchPOs.stream()
                .filter(m -> keyToId.containsKey(uniqueKey(m)))
                .peek(m -> m.setId(keyToId.get(uniqueKey(m))))
                .toList();
    }

    /**
     * 业务唯一键：matchId + tournamentId + year，与 tennis_match 表的唯一索引保持一致
     */
    private static String uniqueKey(TennisMatchPO po) {
        return po.getMatchId() + "|" + po.getTournamentId() + "|" + po.getYear();
    }

    private void saveSetScores(List<Match> matches) {
        List<TennisSetScorePO> allSetScores = new ArrayList<>();
        for (Match match : matches) {
            if (CollectionUtils.isEmpty(match.getSets()) || match.getMatchId() == null) {
                continue;
            }
            for (SetScore setScore : match.getSets()) {
                TennisSetScorePO po = new TennisSetScorePO();
                po.setMatchId(match.getMatchId());
                po.setTournamentId(match.getTournamentId());
                po.setYear(match.getYear());
                po.setSetNumber(setScore.getSetNumber());
                po.setP1Games(setScore.getP1Games());
                po.setP2Games(setScore.getP2Games());
                po.setP1Tiebreak(setScore.getP1Tiebreak());
                po.setP2Tiebreak(setScore.getP2Tiebreak());
                allSetScores.add(po);
            }
        }
        tennisSetScoreService.saveOrUpdateBatch(allSetScores);
    }

    /**
     * 解析 "HH:MM:SS" 格式的比赛时长为分钟数
     */
    private Integer parseDuration(String matchTime) {
        if (matchTime == null || matchTime.isEmpty()) return null;
        try {
            String[] parts = matchTime.split(":");
            if (parts.length == 3) {
                return Integer.parseInt(parts[0].trim()) * 60 + Integer.parseInt(parts[1].trim())
                        + (Integer.parseInt(parts[2].trim()) >= 30 ? 1 : 0);
            }
            // 格式不是 HH:MM:SS（段数不对或含全角冒号）时打日志，避免静默返回 null
            log.warn("解析比赛时长格式异常: value=[{}], len={}, codePoints={}",
                    matchTime, matchTime.length(), matchTime.chars().boxed().toList());
        } catch (NumberFormatException e) {
            log.warn("解析比赛时长失败: value=[{}]", matchTime, e);
        }
        return null;
    }

    /**
     * 合并两个 PlayerTeam 的盘分数据，按 SetNumber 对齐
     */
    private List<TennisSetScorePO> buildSetScores(MatchesResponse.MatchInfo info,
                                                   String tournamentId, Integer year) {
        List<TennisSetScorePO> scores = new ArrayList<>();
        if (info.getPlayerTeam1() == null || info.getPlayerTeam2() == null) return scores;

        List<MatchesResponse.SetInfo> sets1 = info.getPlayerTeam1().getSets();
        List<MatchesResponse.SetInfo> sets2 = info.getPlayerTeam2().getSets();
        if (CollectionUtils.isEmpty(sets1)) return scores;

        for (MatchesResponse.SetInfo s1 : sets1) {
            TennisSetScorePO po = new TennisSetScorePO();
            po.setMatchId(info.getMatchId());
            po.setTournamentId(tournamentId);
            po.setYear(year);
            po.setSetNumber(s1.getSetNumber());
            po.setP1Games(s1.getSetScore() != null ? Integer.parseInt(s1.getSetScore()) : 0);
            po.setP1Tiebreak(s1.getTieBreakScore() != null ? Integer.parseInt(s1.getTieBreakScore()) : null);

            // 查找 PlayerTeam2 同一盘的数据
            if (CollectionUtils.isNotEmpty(sets2)) {
                sets2.stream()
                        .filter(s2 -> s2.getSetNumber() != null && s2.getSetNumber().equals(s1.getSetNumber()))
                        .findFirst()
                        .ifPresent(s2 -> {
                            po.setP2Games(s2.getSetScore() != null ? Integer.parseInt(s2.getSetScore()) : 0);
                            po.setP2Tiebreak(s2.getTieBreakScore() != null ? Integer.parseInt(s2.getTieBreakScore()) : null);
                        });
            }
            scores.add(po);
        }
        return scores;
    }

    private String generateMatchId(String tournamentId, Long drawId, Integer roundNumber,
                                   String Player1Id, String Player2Id) {
        // 使用组合键生成唯一 ID: D_{drawId}_{roundNumber}_{Player1Id}_{Player2Id}
        StringBuilder sb = new StringBuilder();
        sb.append("D");
        if (drawId != null) sb.append(drawId);
        if (roundNumber != null) sb.append("R").append(roundNumber);
        if (Player1Id != null) sb.append("P").append(Player1Id);
        if (Player2Id != null) sb.append("p").append(Player2Id);
        return sb.toString();
    }
}
