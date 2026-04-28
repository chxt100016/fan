package com.chxt.service.dongya;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.Participant;
import com.chxt.config.DongyaMonitorConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class DongyaNotificationFormatter {

    @Resource
    private DongyaMonitorConfig config;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    private static final String[] WEEKDAYS = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public String formatNewMatchNotification(Activity activity) {
        StringBuilder sb = new StringBuilder();

        sb.append(config.getNotice().getTitlePrefix()).append(" 新比赛通知\n\n");

        sb.append("【比赛信息】\n");
        sb.append(formatBasicInfo(activity)).append("\n");

        sb.append(formatParticipants(activity));

        return sb.toString();
    }

    public String formatNewFemaleJoinedNotification(Activity activity, ActivityCacheData cachedData) {
        StringBuilder sb = new StringBuilder();

        sb.append(config.getNotice().getTitlePrefix()).append(" 女生加入通知\n\n");

        sb.append("【比赛信息】\n");
        sb.append(formatBasicInfo(activity)).append("\n");

        sb.append("【新加入的女生】\n");
        Set<Integer> cachedFemaleIds = cachedData.getFemaleParticipantIds();
        List<String> newFemaleNames = activity.getParticipants().stream()
                .filter(p -> p.getGender() != null && p.getGender() == 2)
                .filter(p -> !cachedFemaleIds.contains(p.getId()))
                .map(this::formatParticipantDetail)
                .toList();

        newFemaleNames.forEach(name -> sb.append("• ").append(name).append("\n"));

        return sb.toString();
    }

    private String formatBasicInfo(Activity activity) {
        StringBuilder sb = new StringBuilder();

        String timeInfo = formatTimeInfo(activity.getBeginTime(), activity.getFinishTime());
        sb.append("时间: ").append(timeInfo).append("\n");

        sb.append("地点: ").append(activity.getPlacename()).append("\n");

        if (activity.getFullAddress() != null && !activity.getFullAddress().isEmpty()) {
            sb.append("地址: ").append(activity.getFullAddress()).append("\n");
        }

        sb.append("活动: ").append(activity.getName()).append("\n");

        int currentCount = activity.getParticipantReal() != null ? activity.getParticipantReal() : 0;
        int maxCount = activity.getParticipantMax() != null ? activity.getParticipantMax() : 0;
        sb.append("人数: ").append(currentCount).append("/").append(maxCount).append("\n");

        return sb.toString();
    }

    private String formatTimeInfo(String beginTime, String finishTime) {
        if (beginTime == null || beginTime.isEmpty()) {
            return "未设置";
        }

        try {
            LocalDateTime begin = LocalDateTime.parse(beginTime, DATE_TIME_FORMATTER);
            String beginStr = begin.format(DATE_FORMATTER);
            String weekday = WEEKDAYS[begin.getDayOfWeek().getValue()];

            if (finishTime != null && !finishTime.isEmpty()) {
                LocalDateTime finish = LocalDateTime.parse(finishTime, DATE_TIME_FORMATTER);
                String finishStr = finish.format(DateTimeFormatter.ofPattern("HH:mm"));
                return String.format("%s %s ~ %s", beginStr, weekday, finishStr);
            }

            return beginStr + " " + weekday;
        } catch (Exception e) {
            log.error("解析时间失败: beginTime={}", beginTime, e);
            return beginTime;
        }
    }

    private String formatParticipants(Activity activity) {
        if (activity.getParticipants() == null || activity.getParticipants().isEmpty()) {
            return "【参与者】\n暂无参与者\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【参与者】\n");

        List<String> males = new ArrayList<>();
        List<String> females = new ArrayList<>();

        for (Participant p : activity.getParticipants()) {
            if (p.getGender() != null && p.getGender() == 2) {
                females.add(formatParticipantDetail(p));
            } else {
                males.add(formatParticipantDetail(p));
            }
        }

        if (!males.isEmpty()) {
            sb.append("男生:\n");
            males.forEach(name -> sb.append("• ").append(name).append("\n"));
        }

        if (!females.isEmpty()) {
            sb.append("女生:\n");
            females.forEach(name -> sb.append("• ").append(name).append("\n"));
        }

        return sb.toString();
    }

    private String formatParticipantDetail(Participant p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getName());

        sb.append(" ").append(getGenderText(p.getGender()));

        if (p.getSinglesUtr() != null && p.getSinglesUtr() > 0) {
            sb.append(String.format(" UTR:%.2f", p.getSinglesUtr()));
        } else if (p.getTennisLevel() != null && !p.getTennisLevel().isEmpty()) {
            sb.append(String.format(" 等级:%s", p.getTennisLevel()));
        }

        return sb.toString();
    }

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
}
