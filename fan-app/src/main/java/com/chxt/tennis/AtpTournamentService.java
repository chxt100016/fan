package com.chxt.tennis;

import com.chxt.client.tennistv.TennisTvClient;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.db.tennis.service.TennisTournamentService;
import com.chxt.tennis.convert.TournamentAppConvertMapper;
import com.chxt.tennis.model.Tournament;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AtpTournamentService {

    @Resource
    private TennisTvClient tennisTvClient;

    @Resource
    private TennisTournamentService tennisTournamentService;

    /**
     * 从 live matches 接口采集并保存赛事
     */
    public List<Tournament> collectLiveTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        var response = tennisTvClient.getLiveMatches();
        if (response == null || CollectionUtils.isEmpty(response.getTournaments())) {
            return tournaments;
        }

        tournaments = response.getTournaments().stream()
                .map(TournamentAppConvertMapper.INSTANCE::toTournament)
                .toList();
//        saveTournaments(tournaments);

        return tournaments;
    }

    /**
     * 查询当前时间在 start_date 和 end_date 之间的赛事
     */
    public List<TennisTournamentPO> current() {
        LocalDate today = LocalDate.now();
        return tennisTournamentService.findCurrentTournaments(today);
    }

    /**
     * 拉取并保存指定年份的 ATP 赛事
     */
    public int collect(int year) {
        List<MatchesResponse.TournamentInfo> infos = tennisTvClient.getTournaments(year);
        if (CollectionUtils.isEmpty(infos)) {
            log.warn("从API获取赛事列表为空, year={}", year);
            return 0;
        }
        List<Tournament> tournaments = infos.stream()
                .map(TournamentAppConvertMapper.INSTANCE::toTournament)
                .toList();
        tennisTournamentService.saveOrUpdateBatch(TournamentAppConvertMapper.INSTANCE.toTournamentPOList(tournaments));
        log.info("ATP赛事采集完成: year={}, 数量={}", year, tournaments.size());
        return tournaments.size();
    }
}
