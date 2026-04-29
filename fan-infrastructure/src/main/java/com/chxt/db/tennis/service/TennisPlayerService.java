package com.chxt.db.tennis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.tennis.entity.TennisPlayerPO;
import com.chxt.db.tennis.mapper.TennisPlayerMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TennisPlayerService extends ServiceImpl<TennisPlayerMapper, TennisPlayerPO> {

    /**
     * 批量保存/更新球员（upsert）
     */
    public void saveOrUpdateBatch(List<TennisPlayerPO> players) {
        if (CollectionUtils.isEmpty(players)) {
            return;
        }

        // 查询已存在的球员
        List<String> playerIds = players.stream()
                .map(TennisPlayerPO::getPlayerId)
                .toList();

        Map<String, TennisPlayerPO> existMap = this.lambdaQuery()
                .in(TennisPlayerPO::getPlayerId, playerIds)
                .list()
                .stream()
                .collect(Collectors.toMap(TennisPlayerPO::getPlayerId, p -> p, (a, b) -> a));

        // 分离插入和更新
        List<TennisPlayerPO> toInsert = players.stream()
                .filter(p -> !existMap.containsKey(p.getPlayerId()))
                .toList();

        List<TennisPlayerPO> toUpdate = players.stream()
                .filter(p -> existMap.containsKey(p.getPlayerId()))
                .map(p -> {
                    TennisPlayerPO po = existMap.get(p.getPlayerId());
                    po.setFirstName(p.getFirstName());
                    po.setLastName(p.getLastName());
                    po.setFullName(p.getFullName());
                    po.setNationality(p.getNationality());
                    po.setCountryCode(p.getCountryCode());
                    po.setRank(p.getRank());
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
