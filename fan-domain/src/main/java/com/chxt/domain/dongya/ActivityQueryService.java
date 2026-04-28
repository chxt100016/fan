package com.chxt.domain.dongya;

import com.chxt.client.dongya58.model.Activity;
import com.chxt.domain.dongya.gateway.ActivityQueryGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActivityQueryService {

    @Resource
    private ActivityQueryGateway activityQueryGateway;

    public List<Activity> queryActivities() {
        log.info("开始查询活动数据");
        List<Activity> activities = activityQueryGateway.queryActivities();
        log.info("查询到 {} 个活动", activities.size());
        return activities;
    }
}
