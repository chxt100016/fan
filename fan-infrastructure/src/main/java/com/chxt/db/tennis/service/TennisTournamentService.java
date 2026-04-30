package com.chxt.db.tennis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.db.tennis.mapper.TennisTournamentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TennisTournamentService extends ServiceImpl<TennisTournamentMapper, TennisTournamentPO> {

    /**
     * 批量保存/更新赛事（upsert）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateBatch(List<TennisTournamentPO> tournaments) {
        if (CollectionUtils.isEmpty(tournaments)) {
            return;
        }

        // 查询已存在的赛事（使用 year + tournamentId 作为唯一标识）
        Map<String, TennisTournamentPO> existMap = this.lambdaQuery()
                .list()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.getYear() + "_" + t.getTournamentId(),
                        t -> t,
                        (a, b) -> a
                ));

        // 分离插入和更新
        List<TennisTournamentPO> toInsert = tournaments.stream()
                .filter(t -> !existMap.containsKey(t.getYear() + "_" + t.getTournamentId()))
                .toList();

        List<TennisTournamentPO> toUpdate = tournaments.stream()
                .filter(t -> existMap.containsKey(t.getYear() + "_" + t.getTournamentId()))
                .map(t -> {
                    TennisTournamentPO po = existMap.get(t.getYear() + "_" + t.getTournamentId());
                    po.setName(t.getName());
                    po.setSurface(t.getSurface());
                    po.setCategory(t.getCategory());
                    po.setCity(t.getCity());
                    po.setCountry(t.getCountry());
                    po.setStartDate(t.getStartDate());
                    po.setEndDate(t.getEndDate());
                    po.setStatus(t.getStatus());
                    return po;
                })
                .toList();

        if (CollectionUtils.isNotEmpty(toInsert)) {
            this.saveBatch(toInsert);
            log.info("批量插入赛事: {}条", toInsert.size());
        }
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            this.updateBatchById(toUpdate);
            log.info("批量更新赛事: {}条", toUpdate.size());
        }
    }

    /**
     * 查询进行中的赛事
     */
    public List<TennisTournamentPO> findActive() {
        return this.lambdaQuery()
                .eq(TennisTournamentPO::getStatus, "active")
                .list();
    }

    /**
     * 查询当前时间在 start_date 和 end_date 之间的赛事
     */
    public List<TennisTournamentPO> findCurrentTournaments(LocalDate date) {
        return this.lambdaQuery()
                .le(TennisTournamentPO::getStartDate, date)
                .ge(TennisTournamentPO::getEndDate, date)
                .list();
    }
}
