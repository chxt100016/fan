package com.chxt.domain.dongya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动查询请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequest {

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer limit;

    /**
     * 筛选运动类型：0-网球
     */
    private Integer filterSportType;

    /**
     * 筛选日期
     */
    private String filterDate;

    /**
     * 排序方式
     */
    private Integer sort;

    /**
     * 筛选等级范围，如 "4.5,4.5"
     */
    private String filterMinMaxLevel;

    /**
     * 筛选活动类型
     */
    private Integer filterType;

    /**
     * 筛选赛制
     */
    private Integer filterDivisionFormat;

    /**
     * 筛选认证类型
     */
    private String filterTennisVerifiedType;

    /**
     * 筛选年龄组
     */
    private String filterAgeGroup;

    /**
     * 按时间升序
     */
    private Boolean orderByTimeAsc;

    /**
     * 城市
     */
    private String city;
}
