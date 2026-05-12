package com.chxt.tennis;

import com.chxt.client.tennistv.TennisTvClient;
import com.chxt.client.tennistv.model.AtpDrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.db.tennis.repository.TennisTournamentEntryRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TennisCollectService {

    @Resource
    private TennisTvClient tennisTvClient;

    @Resource
    private TournamentCollectService tournamentCollectService;

    @Resource
    private DrawCollectService drawCollectService;

    @Resource
    private PlayerCollectService playerCollectService;

    @Resource
    private MatchCollectService matchCollectService;



    @Resource
    private TennisTournamentEntryRepository tennisTournamentEntryRepository;

    /**
     * 采集指定年份的赛事列表：ATP（TennisTV）+ WTA
     */
    public void tournaments(int year) {
        tournamentCollectService.collectTournament(year);
    }

    /**
     * 当前签表
     */
    public void currentDraws() {
        List<TennisTournamentPO> tournaments = tournamentCollectService.current();
        if (CollectionUtils.isEmpty(tournaments)) {
            log.info("当前无进行中的赛事");
            return;
        }

        for (TennisTournamentPO tournament : tournaments) {
            try {
                this.draws(tournament.getTour(), tournament.getTournamentId(), tournament.getYear());
            } catch (Exception e) {
                log.error("采集签表失败, tournamentId={}", tournament.getTournamentId(), e);
            }
        }
    }

    /**
     * 指定签表
     */
    public void draws(String tour, String tournamentId, int year) {
        if (tour.equals("ATP")) {
            AtpDrawsResponse response = tennisTvClient.getDraws(tournamentId, year);
            if (response != null && response.getMS() != null && CollectionUtils.isNotEmpty(response.getMS().getRounds())) {


                // 签表
                Long drawId = this.drawCollectService.atp(response, tournamentId, year);
                // 比赛
                this.matchCollectService.atpFromDraw(response, tournamentId, drawId, year);
                // 球员赛事登记
                this.tournamentCollectService.atpTournamentEntry(response, tournamentId, year, drawId, "MS");
                // 球员
                this.playerCollectService.atpFromDraw(response);
            }
        }
    }

    /**
     * order of play
     */
    public void oop() {
        this.matchCollectService.atpFromOop();
    }


    /**
     * 进行中比赛d
     */
    public void liveMatch() {
        this.matchCollectService.atpFromLive();
    }
}
