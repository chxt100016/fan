package com.chxt.db.tennis.repository;

import com.chxt.db.tennis.entity.TennisPlayerPO;
import com.chxt.db.tennis.service.TennisPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TennisPlayerRepository {

    private final TennisPlayerService tennisPlayerService;

    public void saveOrUpdateBatch(List<TennisPlayerPO> players) {
        tennisPlayerService.saveOrUpdateBatch(players);
    }
}
