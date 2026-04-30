package com.chxt.tennis;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.db.tennis.service.TennisPlayerService;
import com.chxt.tennis.convert.PlayerAppConvertMapper;
import com.chxt.tennis.model.Player;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TennisPlayerCollectService {

    @Resource
    private TennisPlayerService tennisPlayerService;

    /**
     * 从 live matches 响应中提取球员
     */
    public List<Player> extractFromLiveMatches(List<MatchesResponse.MatchInfo> matches) {
        List<Player> players = new ArrayList<>();
        if (matches == null) {
            return players;
        }
        for (MatchesResponse.MatchInfo match : matches) {
            if (match.getPlayerTeam1() != null) {
                players.add(PlayerAppConvertMapper.INSTANCE.toPlayer(match.getPlayerTeam1()));
            }
            if (match.getPlayerTeam2() != null) {
                players.add(PlayerAppConvertMapper.INSTANCE.toPlayer(match.getPlayerTeam2()));
            }
        }
        return players;
    }

    /**
     * 从签表 fixture 中提取球员（teamTop + teamBottom）
     */
    public List<Player> extractFromDrawFixture(DrawsResponse.Fixture fixture) {
        List<Player> players = new ArrayList<>();
        if (fixture.getResult() == null) {
            return players;
        }
        if (fixture.getResult().getTeamTop() != null
                && fixture.getResult().getTeamTop().getPlayer() != null) {
            players.add(PlayerAppConvertMapper.INSTANCE
                    .toPlayerFromDraw(fixture.getResult().getTeamTop().getPlayer()));
        }
        if (fixture.getResult().getTeamBottom() != null
                && fixture.getResult().getTeamBottom().getPlayer() != null) {
            players.add(PlayerAppConvertMapper.INSTANCE
                    .toPlayerFromDraw(fixture.getResult().getTeamBottom().getPlayer()));
        }
        return players;
    }

    /**
     * 从 OOP match detail 中提取球员
     */
    public List<Player> extractFromOopMatch(OopResponse.MatchDetail detail) {
        List<Player> players = new ArrayList<>();
        if (detail.getPlayerTeam1() != null) {
            players.add(PlayerAppConvertMapper.INSTANCE.toPlayerFromOop(detail.getPlayerTeam1()));
        }
        if (detail.getPlayerTeam2() != null) {
            players.add(PlayerAppConvertMapper.INSTANCE.toPlayerFromOop(detail.getPlayerTeam2()));
        }
        return players;
    }

    /**
     * 批量保存球员
     */
    public void savePlayers(List<Player> players) {
        if (CollectionUtils.isEmpty(players)) {
            return;
        }
        tennisPlayerService.saveOrUpdateBatch(
                PlayerAppConvertMapper.INSTANCE.toPlayerPOList(players));
    }
}
