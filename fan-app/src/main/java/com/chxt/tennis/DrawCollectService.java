package com.chxt.tennis;

import com.chxt.client.tennistv.model.AtpDrawsResponse;
import com.chxt.db.tennis.repository.TennisDrawRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DrawCollectService {

    @Resource
    private TennisDrawRepository tennisDrawRepository;


    public Long atp(AtpDrawsResponse response, String tournamentId, int year) {
        if (response.getMS() != null && CollectionUtils.isNotEmpty(response.getMS().getRounds())) {
            // 先创建 draw 记录，获取 drawId
            AtpDrawsResponse.Draw msDraw = response.getMS();
            Integer totalRounds = msDraw.getRounds() != null ? msDraw.getRounds().size() : 0;
            return tennisDrawRepository.saveOrUpdate(tournamentId, year, "MS", msDraw.getDrawSize(), totalRounds);
        }
        return null;
    }

}
