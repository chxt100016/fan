package com.chxt.tennis;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.db.tennis.entity.TennisMatchPO;
import com.chxt.db.tennis.entity.TennisSetScorePO;
import com.chxt.db.tennis.service.TennisMatchService;
import com.chxt.db.tennis.service.TennisSetScoreService;
import com.chxt.tennis.convert.DrawMatchAppConvertMapper;
import com.chxt.tennis.convert.MatchAppConvertMapper;
import com.chxt.tennis.convert.OopMatchAppConvertMapper;
import com.chxt.tennis.model.Match;
import com.chxt.tennis.model.SetScore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<Match> buildFromDraw(DrawsResponse.Draw draw, String tournamentId, Long drawId) {
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
                match.setRoundNumber(round.getRoundId());
                match.setRoundName(round.getRoundName());

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

    public List<Match> buildFromOop(OopResponse response) {
        List<Match> allMatches = new ArrayList<>();
        if (response == null || CollectionUtils.isEmpty(response.getOop())) {
            return allMatches;
        }

        for (OopResponse.OopDay oopDay : response.getOop()) {
            if (oopDay.getCourts() == null) {
                continue;
            }
            for (Map.Entry<String, OopResponse.CourtDetail> entry : oopDay.getCourts().entrySet()) {
                OopResponse.CourtDetail court = entry.getValue();
                if (court.getMatches() == null) {
                    continue;
                }
                for (OopResponse.MatchDetail detail : court.getMatches()) {
                    allMatches.add(OopMatchAppConvertMapper.INSTANCE.toMatch(detail));
                }
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

    private void saveSetScores(List<Match> matches) {
        List<TennisSetScorePO> allSetScores = new ArrayList<>();
        for (Match match : matches) {
            if (CollectionUtils.isEmpty(match.getSets()) || match.getMatchId() == null) {
                continue;
            }
            for (SetScore setScore : match.getSets()) {
                TennisSetScorePO po = new TennisSetScorePO();
                po.setMatchId(match.getMatchId());
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
