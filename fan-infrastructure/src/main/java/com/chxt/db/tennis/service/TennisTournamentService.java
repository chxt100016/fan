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

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateBatch(List<TennisTournamentPO> tournaments) {
        if (CollectionUtils.isEmpty(tournaments)) {
            return;
        }

        List<String> tournamentIds = tournaments.stream()
                .map(TennisTournamentPO::getTournamentId)
                .filter(java.util.Objects::nonNull)
                .toList();

        Map<String, TennisTournamentPO> existMap = this.lambdaQuery()
                .in(TennisTournamentPO::getTournamentId, tournamentIds)
                .list()
                .stream()
                .collect(Collectors.toMap(TennisTournamentPO::getTournamentId, t -> t, (a, b) -> a));

        List<TennisTournamentPO> toInsert = tournaments.stream()
                .filter(t -> t.getTournamentId() == null || !existMap.containsKey(t.getTournamentId()))
                .toList();

        List<TennisTournamentPO> toUpdate = tournaments.stream()
                .filter(t -> t.getTournamentId() != null && existMap.containsKey(t.getTournamentId()))
                .map(t -> {
                    TennisTournamentPO po = existMap.get(t.getTournamentId());
                    po.setName(t.getName());
                    po.setTour(t.getTour());
                    po.setCategory(t.getCategory());
                    po.setSurface(t.getSurface());
                    po.setCity(t.getCity());
                    po.setCountry(t.getCountry());
                    po.setPrizeMoney(t.getPrizeMoney());
                    po.setPrizeMoneyText(t.getPrizeMoneyText());
                    po.setStatus(t.getStatus());
                    po.setStartDate(t.getStartDate());
                    po.setEndDate(t.getEndDate());
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

    public List<TennisTournamentPO> findActive() {
        return this.lambdaQuery()
                .eq(TennisTournamentPO::getStatus, "active")
                .list();
    }

    public List<TennisTournamentPO> findCurrentTournaments(LocalDate date) {
        return this.lambdaQuery()
                .le(TennisTournamentPO::getStartDate, date)
                .ge(TennisTournamentPO::getEndDate, date)
                .list();
    }
}
