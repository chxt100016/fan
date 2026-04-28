package com.chxt.activity;

import com.chxt.client.dongya58.Dongya58Client;
import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.model.ActivityRequest;
import com.chxt.domain.dongya.model.ActivityResponse;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
     * 查询活动并返回结构化数据
     *
     * @param request 请求参数
     * @return 活动列表
     */
    public List<ActivityVO> getActivities(ActivityRequest request) {
        try {
            ActivityResponse response = dongya58Client.getActivities(request);

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.info("未查询到活动数据，request={}", request);
                return new ArrayList<>();
            }

            List<Activity> activities = response.getData();
            log.info("查询到 {} 个活动", activities.size());

            return formatToVO(activities);
        } catch (Exception e) {
            log.error("查询活动失败，request={}", request, e);
            throw new RuntimeException("查询活动失败：" + e.getMessage(), e);
        }
    }

    /**
     * 将活动列表转换为 VO 对象列表
     */
    private List<ActivityVO> formatToVO(List<Activity> activities) {
        List<ActivityVO> result = new ArrayList<>();

        for (Activity activity : activities) {
            ActivityVO vo = new ActivityVO();

            // 格式化为一行：MM-dd HH:mm 星期 X - 名称 - 区域 (Max: participantMax 人)
            String formattedTime = formatTimeInfo(activity.getBeginTime(), activity.getFinishTime());
            String district = activity.getDistrict() != null && !activity.getDistrict().isEmpty() ?
                              activity.getDistrict() : "未分区";
            int maxPlayers = activity.getParticipantMax() != null ? activity.getParticipantMax() : 0;

            vo.setLine(String.format("%s - %s - %s (Max: %d人)",
                    formattedTime, activity.getName(), district, maxPlayers));

            // 格式化参与者姓名列表
            if (activity.getParticipants() != null && !activity.getParticipants().isEmpty()) {
                List<String> participantNames = activity.getParticipants().stream()
                        .map(p -> formatParticipantName(p))
                        .toList();
                vo.setParticipants(participantNames);
            } else {
                vo.setParticipants(new ArrayList<>());
            }

            result.add(vo);
        }

        return result;
    }

    /**
     * 格式化时间信息，包含星期几
     */
    private String formatTimeInfo(String beginTime, String finishTime) {
        if (beginTime == null || beginTime.isEmpty()) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String[] weekdays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        try {
            LocalDateTime begin = LocalDateTime.parse(beginTime, formatter);
            String beginStr = begin.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
            String weekday = weekdays[begin.getDayOfWeek().getValue()];

            if (finishTime != null && !finishTime.isEmpty()) {
                LocalDateTime finish = LocalDateTime.parse(finishTime, formatter);
                String finishStr = finish.format(DateTimeFormatter.ofPattern("HH:mm"));
                return String.format("%s %s ~ %s", beginStr, weekday, finishStr);
            }

            return beginStr + " " + weekday;
        } catch (Exception e) {
            return beginTime;
        }
    }

    /**
     * 格式化参与者姓名：姓名 + 性别 + 水平
     * 格式：张三 男 UTR:2.50 或 李四 女 等级:L3
     */
    private String formatParticipantName(com.chxt.domain.dongya.model.Participant p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getName()).append(" ");

        // 性别用文字表示
        sb.append(getGenderText(p.getGender()));

        // 水平和等级（优先显示 UTR）
        if (p.getSinglesUtr() != null && p.getSinglesUtr() > 0) {
            sb.append(String.format("UTR:%.2f", p.getSinglesUtr()));
        } else if (p.getTennisLevel() != null && !p.getTennisLevel().isEmpty()) {
            sb.append(String.format(" 等级:%s", p.getTennisLevel()));
        }

        return sb.toString().trim();
    }

    /**
     * 获取性别文字表示
     */
    private String getGenderText(Integer gender) {
        if (gender == null) {
            return "";
        }
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "";
        };
    }

    /**
     * 活动 VO
     */
    @Data
    public static class ActivityVO {
        private String line;              // 格式化的一行文本
        private List<String> participants; // 参与者列表，每个元素是"姓名 性别 UTR/等级"格式
    }
}
