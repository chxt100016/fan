package com.chxt.tennis;

import com.chxt.client.tennistv.TennisTvClient;
import com.chxt.client.tennistv.model.AtpDrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.db.tennis.entity.TennisTournamentEntryPO;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.db.tennis.repository.TennisTournamentEntryRepository;
import com.chxt.db.tennis.repository.TennisTournamentRepository;
import com.chxt.tennis.convert.TournamentAppConvertMapper;
import com.chxt.tennis.model.Tournament;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TournamentCollectService {

    @Resource
    private TennisTvClient tennisTvClient;

    @Resource
    private TennisTournamentRepository tennisTournamentRepository;

    @Resource
    private TennisTournamentEntryRepository tennisTournamentEntryRepository;

    /**
     * 查询当前时间在 start_date 和 end_date 之间的赛事
     */
    public List<TennisTournamentPO> current() {
        LocalDate today = LocalDate.now();
        return tennisTournamentRepository.findCurrentTournaments(today);
    }

    public void collectTournament(int year) {
        this.atp(year);
        this.wta(year);
    }


    public void atp(int year) {
        List<MatchesResponse.TournamentInfo> infos = tennisTvClient.getTournaments(year);
        if (CollectionUtils.isEmpty(infos)) {
            log.warn("从API获取赛事列表为空, year={}", year);
        }
        List<Tournament> tournaments = infos.stream()
                .map(TournamentAppConvertMapper.INSTANCE::toTournament)
                .toList();
        tennisTournamentRepository.saveOrUpdateBatch(TournamentAppConvertMapper.INSTANCE.toTournamentPOList(tournaments));
        log.info("ATP赛事采集完成: year={}, 数量={}", year, tournaments.size());
    }

    public void wta(int year) {

    }

    public void atpTournamentEntry(AtpDrawsResponse response, String tournamentId, int year, Long drawId, String drawType) {
        AtpDrawsResponse.Draw draw = response.getMS();
        // key: playerId，value: TennisTournamentEntryPO
        Map<String, TennisTournamentEntryPO> entryMap = new LinkedHashMap<>();

        if (draw == null || CollectionUtils.isEmpty(draw.getRounds())) {
            return;
        }

        for (AtpDrawsResponse.Round round : draw.getRounds()) {
            if (CollectionUtils.isEmpty(round.getFixtures())) {
                continue;
            }
            for (AtpDrawsResponse.Fixture fixture : round.getFixtures()) {
                extractEntriesFromDrawLine(fixture.getDrawLineTop(), tournamentId, year, drawId, drawType, entryMap);
                extractEntriesFromDrawLine(fixture.getDrawLineBottom(), tournamentId, year, drawId, drawType, entryMap);
            }
        }

        tennisTournamentEntryRepository.saveEntries(new ArrayList<>(entryMap.values()));

    }

    /**
     * 从 DrawLine 中提取球员种子信息
     */
    private void extractEntriesFromDrawLine(
            AtpDrawsResponse.DrawLine drawLine, String tournamentId, int year,
            Long drawId, String drawType, Map<String, TennisTournamentEntryPO> entryMap) {
        if (drawLine == null || CollectionUtils.isEmpty(drawLine.getPlayers())) {
            return;
        }

        for (AtpDrawsResponse.PlayerInfo playerInfo : drawLine.getPlayers()) {
            if (playerInfo == null || playerInfo.getPlayerId() == null) {
                continue;
            }
            TennisTournamentEntryPO entry = new TennisTournamentEntryPO();
            entry.setTournamentId(tournamentId);
            entry.setYear(year);
            entry.setPlayerId(playerInfo.getPlayerId());
            entry.setDrawId(drawId);
            entry.setDrawType(drawType);
            entry.setSeed(drawLine.getSeed() != null ? drawLine.getSeed().shortValue() : null);
            // 相同 playerId 覆盖，保留最新的种子数
            entryMap.put(playerInfo.getPlayerId(), entry);
        }
    }
}
