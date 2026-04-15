package com.chxt.client.dongya58;

import com.chxt.client.dongya58.model.ActivityRequest;
import com.chxt.client.dongya58.model.ActivityResponse;
import com.chxt.domain.utils.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 动呀 58 API 客户端
 */
@Slf4j
@Component
public class Dongya58Client {

    private static final String API_URI = "https://api.dongya58.com/v1/activities";

    /**
     * 查询活动列表
     */
    public ActivityResponse getActivities(ActivityRequest request) {
        Http http = Http.uri(API_URI)
                .header("Host", "api.dongya58.com")
                .header("language", "zh")
                .header("X-Auth-Token", "dy-weixin")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3ODQwMjA1MDgsIlVpZCI6MTEzOTk3LCJVZWlkIjozMzYyOSwiTGNpZCI6MCwiQ2NpZCI6MCwiQWRtaW5JZCI6MH0.-k1doT8oWFnluGUA5CFx1sIB6oaFe0-emPiZEb-vVEc")
                .header("longitude", "120.29850006103516")
                .header("latitude", "30.418750762939453")
                .header("xweb_xhr", "1")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 MicroMessenger/7.0.20.1781(0x6700143B) NetType/WIFI MiniProgramEnv/Mac MacWechat/WMPF MacWechat/3.8.7(0x13080712) UnifiedPCMacWechat(0xf264186a) XWEB/19708")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "cross-site")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Referer", "https://servicewechat.com/wx1d006dda1aaa917c/30/page-frame.html")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .jsonHeader();

        // 透传请求参数
        if (request.getPage() != null) {
            http = http.param("page", String.valueOf(request.getPage()));
        }
        if (request.getLimit() != null) {
            http = http.param("limit", String.valueOf(request.getLimit()));
        }
        if (request.getFilterSportType() != null) {
            http = http.param("filterSportType", String.valueOf(request.getFilterSportType()));
        }
        if (request.getFilterDate() != null && !request.getFilterDate().isEmpty()) {
            http = http.param("filterDate", request.getFilterDate());
        }
        if (request.getSort() != null) {
            http = http.param("sort", String.valueOf(request.getSort()));
        }
        if (request.getFilterMinMaxLevel() != null && !request.getFilterMinMaxLevel().isEmpty()) {
            http = http.param("filterMinMaxLevel", request.getFilterMinMaxLevel());
        }
        if (request.getFilterType() != null) {
            http = http.param("filterType", String.valueOf(request.getFilterType()));
        }
        if (request.getFilterDivisionFormat() != null) {
            http = http.param("filterDivisionFormat", String.valueOf(request.getFilterDivisionFormat()));
        }
        if (request.getFilterTennisVerifiedType() != null && !request.getFilterTennisVerifiedType().isEmpty()) {
            http = http.param("filterTennisVerifiedType", request.getFilterTennisVerifiedType());
        }
        if (request.getFilterAgeGroup() != null && !request.getFilterAgeGroup().isEmpty()) {
            http = http.param("filterAgeGroup", request.getFilterAgeGroup());
        }
        if (request.getOrderByTimeAsc() != null) {
            http = http.param("orderByTimeAsc", String.valueOf(request.getOrderByTimeAsc()));
        }
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            http = http.param("city", request.getCity());
        }

        return http.doGet().result(ActivityResponse.class);
    }
}
