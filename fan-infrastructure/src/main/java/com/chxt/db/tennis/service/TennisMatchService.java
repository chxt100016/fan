package com.chxt.db.tennis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.tennis.entity.TennisMatchPO;
import com.chxt.db.tennis.mapper.TennisMatchMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TennisMatchService extends ServiceImpl<TennisMatchMapper, TennisMatchPO> {

    /**
     * 批量保存/更新比赛（upsert）
     */
    public void saveOrUpdateBatch(List<TennisMatchPO> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }

        // 查询已存在的比赛
        List<String> matchIds = matches.stream()
                .map(TennisMatchPO::getMatchId)
                .toList();

        Map<String, TennisMatchPO> existMap = this.lambdaQuery()
                .in(TennisMatchPO::getMatchId, matchIds)
                .list()
                .stream()
                .collect(Collectors.toMap(TennisMatchPO::getMatchId, m -> m, (a, b) -> a));

        // 分离插入和更新
        List<TennisMatchPO> toInsert = matches.stream()
                .filter(m -> !existMap.containsKey(m.getMatchId()))
                .toList();

        List<TennisMatchPO> toUpdate = matches.stream()
                .filter(m -> existMap.containsKey(m.getMatchId()))
                .map(m -> {
                    TennisMatchPO po = existMap.get(m.getMatchId());
                    po.setScore(m.getScore());
                    po.setSetsScore(m.getSetsScore());
                    po.setStatus(m.getStatus());
                    po.setWinnerId(m.getWinnerId());
                    po.setCourtName(m.getCourtName());
                    po.setNotBeforeTime(m.getNotBeforeTime());
                    po.setNotBeforeText(m.getNotBeforeText());
                    po.setMatchTime(m.getMatchTime());
                    return po;
                })
                .toList();

        if (CollectionUtils.isNotEmpty(toInsert)) {
            this.saveBatch(toInsert);
            log.info("批量插入比赛: {}条", toInsert.size());
        }
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            this.updateBatchById(toUpdate);
            log.info("批量更新比赛: {}条", toUpdate.size());
        }
    }

    /**
     * 查询进行中的比赛
     */
    public List<TennisMatchPO> findActiveByTournament(String tournamentId) {
        return this.lambdaQuery()
                .eq(TennisMatchPO::getTournamentId, tournamentId)
                .in(TennisMatchPO::getStatus, "live", "scheduled")
                .list();
    }

    /**
     * 是否有进行中的比赛
     */
    public boolean hasActiveMatches() {
        return this.lambdaQuery()
                .in(TennisMatchPO::getStatus, "live", "scheduled")
                .exists();
    }
}
