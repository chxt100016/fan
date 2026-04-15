package com.chxt.activity;

import com.chxt.client.dongya58.Dongya58Client;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.ActivityRequest;
import com.chxt.client.dongya58.model.ActivityResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动查询服务
 */
@Slf4j
@Service
public class ActivityService {

    @Resource
    private Dongya58Client dongya58Client;

    /**
     * 查询活动并格式化为 markdown
     * 返回合并的 markdown 内容
     *
     * @param request 请求参数
     * @return markdown 字符串
     */
    public String getActivitiesMarkdown(ActivityRequest request) {
        try {
            ActivityResponse response = dongya58Client.getActivities(request);

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.info("未查询到活动数据，request={}", request);
                return "";
            }

            List<Activity> activities = response.getData();
            log.info("查询到 {} 个活动", activities.size());

            return ActivityFormatter.format(activities);
        } catch (Exception e) {
            log.error("查询活动失败，request={}", request, e);
            throw new RuntimeException("查询活动失败：" + e.getMessage(), e);
        }
    }
}
