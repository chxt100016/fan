package com.chxt.domain.tennis.gateway;

import com.chxt.domain.tennis.model.MatchData;
import com.chxt.domain.tennis.model.PlayerData;
import com.chxt.domain.tennis.model.SetScoreData;

import java.util.List;

/**
 * 比赛查询 Gateway 接口
 */
public interface MatchQueryGateway {

    /**
     * 根据 tournamentId 列表查询比赛
     * @param tournamentIds tournamentId 列表
     * @return 比赛列表
     */
    List<MatchData> listByTournamentIds(List<String> tournamentIds);

    /**
     * 根据比赛ID列表查询盘分
     * @param matchIds 比赛ID列表
     * @return 盘分列表
     */
    List<SetScoreData> listSetScoresByMatchIds(List<String> matchIds);

    /**
     * 根据球员ID列表查询球员信息
     * @param playerIds 球员ID列表
     * @return 球员列表
     */
    List<PlayerData> listPlayersByPlayerIds(List<String> playerIds);
}
