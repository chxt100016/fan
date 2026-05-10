package com.chxt.db.tennis.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chxt.db.tennis.entity.TennisMatchPO;
import com.chxt.db.tennis.entity.TennisPlayerPO;
import com.chxt.db.tennis.entity.TennisSetScorePO;
import com.chxt.db.tennis.mapper.TennisMatchMapper;
import com.chxt.db.tennis.mapper.TennisPlayerMapper;
import com.chxt.db.tennis.mapper.TennisSetScoreMapper;
import com.chxt.domain.tennis.gateway.MatchQueryGateway;
import com.chxt.domain.tennis.model.MatchData;
import com.chxt.domain.tennis.model.PlayerData;
import com.chxt.domain.tennis.model.SetScoreData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 比赛查询 Gateway 实现
 */
@Component
@RequiredArgsConstructor
public class MatchQueryGatewayImpl implements MatchQueryGateway {

    private final TennisMatchMapper matchMapper;
    private final TennisPlayerMapper playerMapper;
    private final TennisSetScoreMapper setScoreMapper;

    @Override
    public List<MatchData> listByTournamentIds(List<String> tournamentIds) {
        if (CollectionUtils.isEmpty(tournamentIds)) {
            return List.of();
        }
        List<TennisMatchPO> list = matchMapper.selectList(
                new LambdaQueryWrapper<TennisMatchPO>()
                        .in(TennisMatchPO::getTournamentId, tournamentIds)

        );
        return list.stream().map(this::toMatchData).toList();
    }

    @Override
    public List<SetScoreData> listSetScoresByMatchIds(List<String> matchIds) {
        if (CollectionUtils.isEmpty(matchIds)) {
            return List.of();
        }
        List<TennisSetScorePO> list = setScoreMapper.selectList(
                new LambdaQueryWrapper<TennisSetScorePO>()
                        .in(TennisSetScorePO::getMatchId, matchIds)
                        .orderByAsc(TennisSetScorePO::getSetNumber)
        );
        return list.stream().map(this::toSetScoreData).toList();
    }

    @Override
    public List<PlayerData> listPlayersByPlayerIds(List<String> playerIds) {
        if (CollectionUtils.isEmpty(playerIds)) {
            return List.of();
        }
        List<TennisPlayerPO> list = playerMapper.selectList(
                new LambdaQueryWrapper<TennisPlayerPO>()
                        .in(TennisPlayerPO::getPlayerId, playerIds)
        );
        return list.stream().map(this::toPlayerData).toList();
    }

    private MatchData toMatchData(TennisMatchPO po) {
        MatchData data = new MatchData();
        data.setMatchId(po.getMatchId());
        data.setTournamentId(po.getTournamentId());
        data.setPlayer1Id(po.getPlayer1Id());
        data.setPlayer2Id(po.getPlayer2Id());
        data.setWinnerId(po.getWinnerId());
        data.setRoundName(po.getRoundName());
        data.setCourt(po.getCourt());
        data.setStatus(po.getStatus());
        data.setDurationMinutes(po.getDurationMinutes());
        data.setScheduledAtText(po.getScheduledAtText());
        data.setMatchDate(po.getMatchDate());
        data.setScheduledAt(po.getScheduledAt());
        data.setStartedAt(po.getStartedAt());
        return data;
    }

    private SetScoreData toSetScoreData(TennisSetScorePO po) {
        SetScoreData data = new SetScoreData();
        data.setMatchId(po.getMatchId());
        data.setSetNumber(po.getSetNumber());
        data.setP1Games(po.getP1Games());
        data.setP2Games(po.getP2Games());
        data.setP1Tiebreak(po.getP1Tiebreak());
        data.setP2Tiebreak(po.getP2Tiebreak());
        return data;
    }

    private PlayerData toPlayerData(TennisPlayerPO po) {
        PlayerData data = new PlayerData();
        data.setPlayerId(po.getPlayerId());
        data.setFirstName(po.getFirstName());
        data.setLastName(po.getLastName());
        data.setNationality(po.getNationality());
        return data;
    }
}
