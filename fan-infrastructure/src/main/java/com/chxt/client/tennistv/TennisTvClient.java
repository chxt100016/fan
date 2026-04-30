package com.chxt.client.tennistv;

import com.chxt.client.tennistv.model.DrawsResponse;
import com.chxt.client.tennistv.model.MatchesResponse;
import com.chxt.client.tennistv.model.OopResponse;
import com.chxt.domain.utils.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TennisTvClient {

    private static final String BASE_URI = "https://api.tennistv.com/tennis/v1";

    /**
     * 接口1: 查询最近比赛 (status=L 表示进行中)
     */
    public MatchesResponse getLiveMatches() {
        try {
            Http http = Http.uri(BASE_URI + "/matches")
                    .param("status", "L")
                    .header("origin", "https://www.tennistv.com")
                    .doGet();

            return http.result(MatchesResponse.class);
        } catch (Exception e) {
            log.error("获取最近比赛失败", e);
            return null;
        }
    }

    /**
     * 接口2: 查询签表数据
     */
    public DrawsResponse getDraws(String tournamentId, int year) {
        try {
            return Http.uri(BASE_URI + "/tournaments/" + tournamentId + "/" + year + "/draws")
                    .doGet()
                    .result(DrawsResponse.class);
        } catch (Exception e) {
            log.error("获取签表数据失败, tournamentId={}, year={}", tournamentId, year, e);
            return null;
        }
    }

    /**
     * 接口3: 查询比赛详情 (每日安排)
     */
    public OopResponse getOop() {
        try {
            return Http.uri(BASE_URI + "/oop")
                    .doGet()
                    .result(OopResponse.class);
        } catch (Exception e) {
            log.error("获取比赛详情失败", e);
            return null;
        }
    }
}
