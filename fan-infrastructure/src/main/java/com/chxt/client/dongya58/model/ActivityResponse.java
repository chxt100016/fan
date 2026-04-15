package com.chxt.client.dongya58.model;

import lombok.Data;
import java.util.List;

/**
 * 活动查询响应
 */
@Data
public class ActivityResponse {

    private Integer page;

    private Integer limit;

    private List<Activity> data;
}
