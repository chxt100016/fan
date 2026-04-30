package com.chxt.tennis;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.db.tennis.service.TennisMatchService;
import com.chxt.tennis.convert.DrawMatchAppConvertMapper;
import com.chxt.tennis.convert.MatchAppConvertMapper;
import com.chxt.tennis.convert.OopMatchAppConvertMapper;
import com.chxt.tennis.model.Match;
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

    /**
     * 从 live matches 响应中转换比赛列表
     */
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

    /**
     * 从签表 rounds/fixtures 中构建比赛列表
     */
    public List<Match> buildFromDraw(DrawsResponse.Draw draw, String tournamentId) {
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
                match.setDrawType(draw.getEventTypeCode());
                match.setRound(round.getRoundName());
                allMatches.add(match);
            }
        }
        return allMatches;
    }

    /**
     * 从 OOP 数据中构建比赛列表
     */
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

    /**
     * 批量保存比赛
     */
    public void saveMatches(List<Match> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }
        tennisMatchService.saveOrUpdateBatch(
                MatchAppConvertMapper.INSTANCE.toMatchPOList(matches));
    }
}
