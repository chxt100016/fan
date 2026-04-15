package com.chxt.web.activity;

import com.chxt.activity.ActivityService;
import com.chxt.client.dongya58.model.ActivityRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 活动查询 Controller
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    /**
     * 查询活动数据并下载 markdown 文件
     * 所有活动合并到一个文件中，按 district 归类展示
     */
    @GetMapping("/query")
    public void query(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @RequestParam(value = "filterSportType", required = false) Integer filterSportType,
            @RequestParam(value = "filterDate", required = false) String filterDate,
            @RequestParam(value = "sort", required = false) Integer sort,
            @RequestParam(value = "filterMinMaxLevel", required = false) String filterMinMaxLevel,
            @RequestParam(value = "filterType", required = false) Integer filterType,
            @RequestParam(value = "filterDivisionFormat", required = false) Integer filterDivisionFormat,
            @RequestParam(value = "filterTennisVerifiedType", required = false) String filterTennisVerifiedType,
            @RequestParam(value = "filterAgeGroup", required = false) String filterAgeGroup,
            @RequestParam(value = "orderByTimeAsc", required = false) Boolean orderByTimeAsc,
            @RequestParam(value = "city", required = false) String city,
            HttpServletResponse response) {

        ActivityRequest request = ActivityRequest.builder()
                .page(page)
                .limit(limit)
                .filterSportType(filterSportType)
                .filterDate(filterDate)
                .sort(sort)
                .filterMinMaxLevel(filterMinMaxLevel)
                .filterType(filterType)
                .filterDivisionFormat(filterDivisionFormat)
                .filterTennisVerifiedType(filterTennisVerifiedType)
                .filterAgeGroup(filterAgeGroup)
                .orderByTimeAsc(orderByTimeAsc)
                .city(city)
                .build();

        String markdown = activityService.getActivitiesMarkdown(request);
        write(markdown, response);
    }

    @SneakyThrows
    private void write(String content, HttpServletResponse response) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        String fileName = URLEncoder.encode("activities.md", StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/markdown");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLength(bytes.length);

        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(bytes);
            out.flush();
        }
    }
}
