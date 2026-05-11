package com.chxt.tennis;

import com.chxt.client.tennistv.TennisTvClient;
import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.db.tennis.entity.TennisTournamentPO;
import com.chxt.db.tennis.entity.TennisTournamentEntryPO;
import com.chxt.db.tennis.service.TennisDrawService;
import com.chxt.db.tennis.service.TennisTournamentEntryService;
import com.chxt.tennis.convert.OopMatchAppConvertMapper;
import com.chxt.tennis.model.Match;
import com.chxt.tennis.model.Player;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TennisCollectService {

    @Resource
    private TennisTvClient tennisTvClient;

    @Resource
    private AtpTournamentService atpTournamentService;

    @Resource
    private AtpPlayerService atpPlayerService;

    @Resource
    private AtpMatchService atpMatchService;

    @Resource
    private TennisDrawService tennisDrawService;

    @Resource
    private WtaTournamentService wtaTournamentService;

    @Resource
    private TennisTournamentEntryService tennisTournamentEntryService;

    /**
     * 采集指定年份的赛事列表：ATP（TennisTV）+ WTA
     */
    public int tournaments(int year) {
        int atpSize = atpTournamentService.collect(year);
        int wtaSize = wtaTournamentService.collect(year);
        log.info("赛事采集完成: year={}, ATP={}, WTA={}", year, atpSize, wtaSize);
        return atpSize + wtaSize;
    }


    public void currentDraws() {
        List<TennisTournamentPO> tournaments = atpTournamentService.current();

        if (CollectionUtils.isEmpty(tournaments)) {
            log.info("当前无进行中的赛事");
            return;
        }

        for (TennisTournamentPO tournament : tournaments) {
            try {
                int year = tournament.getYear() != null ? tournament.getYear()
                        : (tournament.getStartDate() != null ? tournament.getStartDate().getYear() : java.time.LocalDate.now().getYear());
                this.draws(tournament.getTournamentId(), year);
            } catch (Exception e) {
                log.error("采集签表失败, tournamentId={}", tournament.getTournamentId(), e);
            }
        }
    }

    public void draws(String tournamentId, int year) {
        DrawsResponse response = tennisTvClient.getDraws(tournamentId, year);
        if (response == null) {
            log.warn("签表数据为空");
            return;
        }

        List<Player> allPlayers = new ArrayList<>();
        List<Match> allMatches = new ArrayList<>();

        if (response.getMS() != null && CollectionUtils.isNotEmpty(response.getMS().getRounds())) {
            // 先创建 draw 记录，获取 drawId
            DrawsResponse.Draw msDraw = response.getMS();
            Integer totalRounds = msDraw.getRounds() != null ? msDraw.getRounds().size() : 0;
            Long drawId = tennisDrawService.saveOrUpdate(
                    tournamentId, year, "MS", msDraw.getDrawSize(), totalRounds);

            allMatches.addAll(atpMatchService.buildFromDraw(msDraw, tournamentId, drawId, year));

            // 从第一轮签表中提取种子球员数据
            List<TennisTournamentEntryPO> entries = extractEntriesFromDraw(
                    msDraw, tournamentId, year, drawId, "MS");

            for (DrawsResponse.Round round : response.getMS().getRounds()) {
                if (CollectionUtils.isEmpty(round.getFixtures())) {
                    continue;
                }
                for (DrawsResponse.Fixture fixture : round.getFixtures()) {
                    allPlayers.addAll(atpPlayerService.extractFromDrawFixture(fixture));
                }
            }

            // 保存种子球员数据
            if (CollectionUtils.isNotEmpty(entries)) {
                tennisTournamentEntryService.saveEntries(entries);
            }
        }

        atpPlayerService.savePlayers(allPlayers);
        atpMatchService.saveMatches(allMatches);

        log.info("签表采集完成: 球员={}, 比赛={}", allPlayers.size(), allMatches.size());
    }

    /**
     * 从签表所有轮次中提取种子球员数据，用 Map 去重，重复的覆盖
     */
    private List<TennisTournamentEntryPO> extractEntriesFromDraw(
            DrawsResponse.Draw draw, String tournamentId, int year, Long drawId, String drawType) {
        // key: playerId，value: TennisTournamentEntryPO
        Map<String, TennisTournamentEntryPO> entryMap = new LinkedHashMap<>();

        if (draw == null || CollectionUtils.isEmpty(draw.getRounds())) {
            return new ArrayList<>(entryMap.values());
        }

        for (DrawsResponse.Round round : draw.getRounds()) {
            if (CollectionUtils.isEmpty(round.getFixtures())) {
                continue;
            }
            for (DrawsResponse.Fixture fixture : round.getFixtures()) {
                extractEntriesFromDrawLine(fixture.getDrawLineTop(), tournamentId, year, drawId, drawType, entryMap);
                extractEntriesFromDrawLine(fixture.getDrawLineBottom(), tournamentId, year, drawId, drawType, entryMap);
            }
        }

        return new ArrayList<>(entryMap.values());
    }

    /**
     * 从 DrawLine 中提取球员种子信息
     */
    private void extractEntriesFromDrawLine(
            DrawsResponse.DrawLine drawLine, String tournamentId, int year,
            Long drawId, String drawType, Map<String, TennisTournamentEntryPO> entryMap) {
        if (drawLine == null || CollectionUtils.isEmpty(drawLine.getPlayers())) {
            return;
        }

        for (DrawsResponse.PlayerInfo playerInfo : drawLine.getPlayers()) {
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





    /**
     * 采集进行中比赛（status=P），更新时长、状态、盘分、场地、球员
     */
    public void liveMatch() {
        log.info("开始采集进行中比赛");
        MatchesResponse response = tennisTvClient.getMatchesByStatus("L");
        if (response == null || CollectionUtils.isEmpty(response.getMatches())) {
            log.info("无进行中的比赛");
            return;
        }
        atpMatchService.updateLiveMatches(response.getMatches());
    }

    public void currentMatch() {
        log.info("开始采集比赛详情");

        List<OopResponse> oop = tennisTvClient.getOop();
        if (CollectionUtils.isEmpty(oop)) {
            return;
        }

        List<Match> allMatches = new ArrayList<>();
        for (OopResponse tournament : oop) {
            if (CollectionUtils.isEmpty(tournament.getOop())) {
                continue;
            }
            for (OopResponse.OopDay day : tournament.getOop()) {
                if (day.getCourts() == null) {
                    continue;
                }
                for (OopResponse.CourtDetail court : day.getCourts().values()) {
                    if (CollectionUtils.isEmpty(court.getMatches())) {
                        continue;
                    }

                    java.time.LocalDateTime lastMatchScheduledAt = null;

                    for (OopResponse.MatchDetail detail : court.getMatches()) {
                        if (!detail.getAssociationCode().equals("ATP")) {
                            continue;
                        }
                        Match match = OopMatchAppConvertMapper.INSTANCE.toMatch(detail);

                        // 处理 "Followed By" 的情况：使用上一场比赛时间 + 70分钟
                        if ("Followed By".equals(detail.getNotBeforeText()) && match.getScheduledAt() == null) {
                            if (lastMatchScheduledAt != null) {
                                match.setScheduledAt(lastMatchScheduledAt.plusMinutes(70));
                            }
                        }

                        // 更新上一场比赛的时间
                        if (match.getScheduledAt() != null) {
                            lastMatchScheduledAt = match.getScheduledAt();
                        }

                        allMatches.add(match);
                    }
                }
            }
        }

        if (CollectionUtils.isEmpty(allMatches)) {
            log.info("OOP中无比赛数据");
            return;
        }

        atpMatchService.saveMatches(allMatches);
        log.info("比赛详情采集完成: 数量={}", allMatches.size());
    }
}
