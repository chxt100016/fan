package com.chxt.client.wta;

import com.chxt.client.wta.model.WtaTournamentsResponse;
import com.chxt.domain.utils.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WtaClient {

    private static final String BASE_URI = "https://api.wtatennis.com/tennis/tournaments";

    public WtaTournamentsResponse getTournaments(int year) {
        try {
            return Http.uri(BASE_URI)
                    .param("page", "0")
                    .param("pageSize", "1000")
                    .param("excludeLevels", "ITF")
                    .param("from", year + "-01-01")
                    .param("to", year + "-12-31")
                    .doGet()
                    .result(WtaTournamentsResponse.class);
        } catch (Exception e) {
            log.error("获取WTA赛事列表失败, year={}", year, e);
            return null;
        }
    }
}
