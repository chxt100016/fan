package com.chxt.web.activity;

import com.chxt.activity.ActivityService;
import com.chxt.activity.ActivityService.ActivityVO;
import com.chxt.domain.dongya.model.ActivityRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 活动查询 Controller
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @GetMapping("/query")
    public List<ActivityVO> query(
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
            @RequestParam(value = "city", required = false) String city) {

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

        return activityService.getActivities(request);
    }

}
