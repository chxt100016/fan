package com.chxt.db.tennis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.tennis.entity.TennisPlayerPO;
import com.chxt.db.tennis.mapper.TennisPlayerMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TennisPlayerService extends ServiceImpl<TennisPlayerMapper, TennisPlayerPO> {

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateBatch(List<TennisPlayerPO> players) {
        if (CollectionUtils.isEmpty(players)) {
            return;
        }

        // 按 playerId 去重，保留最后出现的（更新的数据）
        players = new java.util.ArrayList<>(players.stream()
                .filter(p -> p.getPlayerId() != null)
                .collect(Collectors.toMap(
                        TennisPlayerPO::getPlayerId,
                        p -> p,
                        (a, b) -> b
                ))
                .values());

        List<String> playerIds = players.stream()
                .map(TennisPlayerPO::getPlayerId)
                .filter(java.util.Objects::nonNull)
                .toList();

        Map<String, TennisPlayerPO> existMap = this.lambdaQuery()
                .in(TennisPlayerPO::getPlayerId, playerIds)
                .list()
                .stream()
                .collect(Collectors.toMap(TennisPlayerPO::getPlayerId, p -> p, (a, b) -> a));

        List<TennisPlayerPO> toInsert = players.stream()
                .filter(p -> p.getPlayerId() == null || !existMap.containsKey(p.getPlayerId()))
                .toList();

        List<TennisPlayerPO> toUpdate = players.stream()
                .filter(p -> p.getPlayerId() != null && existMap.containsKey(p.getPlayerId()))
                .map(p -> {
                    TennisPlayerPO po = existMap.get(p.getPlayerId());
                    po.setFirstName(p.getFirstName());
                    po.setLastName(p.getLastName());
                    po.setNationality(p.getNationality());
                    po.setBirthDate(p.getBirthDate());
                    po.setGender(p.getGender());
                    po.setRanking(p.getRanking());
                    po.setHand(p.getHand());
                    return po;
                })
                .toList();

        if (CollectionUtils.isNotEmpty(toInsert)) {
            this.saveBatch(toInsert);
            log.info("批量插入球员: {}条", toInsert.size());
        }
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            this.updateBatchById(toUpdate);
            log.info("批量更新球员: {}条", toUpdate.size());
        }
    }
}
