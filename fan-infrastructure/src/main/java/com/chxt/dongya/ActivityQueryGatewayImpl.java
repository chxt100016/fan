package com.chxt.dongya;

import com.chxt.client.dongya58.Dongya58Client;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.ActivityRequest;
import com.chxt.client.dongya58.model.ActivityResponse;
import com.chxt.domain.dongya.gateway.ActivityQueryGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ActivityQueryGatewayImpl implements ActivityQueryGateway {

    @Resource
    private Dongya58Client dongya58Client;

    @Override
    public List<Activity> queryActivities() {
        try {
            ActivityRequest request = ActivityRequest.builder()
                    .page(1)
                    .limit(100)
                    .filterSportType(0)
                    .filterMinMaxLevel("4.5,4.5")
                    .filterType(4)
                    .filterDivisionFormat(1)
                    .city("杭州")
                    .build();

            ActivityResponse response = dongya58Client.getActivities(request);

            if (response == null || response.getData() == null) {
                return new ArrayList<>();
            }

            return response.getData();
        } catch (Exception e) {
            log.error("获取活动数据失败", e);
            return new ArrayList<>();
        }
    }
}
