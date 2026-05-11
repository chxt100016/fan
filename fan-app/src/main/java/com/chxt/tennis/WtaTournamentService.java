package com.chxt.tennis;

import com.chxt.client.wta.WtaClient;
import com.chxt.client.wta.model.WtaTournamentsResponse;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.db.tennis.service.TennisTournamentService;
import com.chxt.tennis.convert.WtaTournamentAppConvertMapper;
import com.chxt.tennis.model.Tournament;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WtaTournamentService {

    @Resource
    private WtaClient wtaClient;

    @Resource
    private TennisTournamentService tennisTournamentService;

    /**
     * 拉取并保存指定年份的 WTA 赛事
     */
    public int collect(int year) {
        WtaTournamentsResponse response = wtaClient.getTournaments(year);
        if (response == null || CollectionUtils.isEmpty(response.getContent())) {
            log.warn("从WTA API获取赛事列表为空, year={}", year);
            return 0;
        }

        List<Tournament> tournaments = response.getContent().stream()
                .map(WtaTournamentAppConvertMapper.INSTANCE::toTournament)
                .toList();
        List<TennisTournamentPO> poList = WtaTournamentAppConvertMapper.INSTANCE.toTournamentPOList(tournaments);
        tennisTournamentService.saveOrUpdateBatch(poList);

        log.info("WTA赛事采集完成: year={}, 数量={}", year, poList.size());
        return poList.size();
    }
}
