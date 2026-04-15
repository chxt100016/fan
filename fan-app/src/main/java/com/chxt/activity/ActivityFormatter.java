package com.chxt.activity;

import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.Participant;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动 Markdown 格式化器
 */
@Slf4j
public class ActivityFormatter {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] WEEKDAY_MAP = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    /**
     * 格式化活动数据为 markdown
     * 按 district 分组展示，返回单个 markdown 字符串
     */
    public static String format(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            return "";
        }

        // 按 district 分组
        Map<String, List<Activity>> grouped = activities.stream()
                .collect(Collectors.groupingBy(
                        Activity::getDistrict,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        StringBuilder sb = new StringBuilder();
        sb.append("# 活动列表\n\n");
        sb.append("共 ").append(activities.size()).append(" 个活动，分布在 ").append(grouped.size()).append(" 个区域\n\n");
        sb.append("---\n\n");

        for (Map.Entry<String, List<Activity>> entry : grouped.entrySet()) {
            String district = entry.getKey();
            String safeDistrict = district != null && !district.isEmpty() ? district : "未分区";

            // 区域标题
            sb.append("## ").append(safeDistrict).append("\n\n");
            sb.append("该区域共 ").append(entry.getValue().size()).append(" 个活动\n\n");

            for (Activity activity : entry.getValue()) {
                sb.append(buildActivitySection(activity));
            }

            sb.append("---\n\n");
        }

        return sb.toString();
    }

    /**
     * 构建单个活动的 markdown 段落
     */
    private static String buildActivitySection(Activity activity) {
        StringBuilder sb = new StringBuilder();

        // 活动名称
        sb.append("### ").append(escapeMarkdown(activity.getName())).append("\n\n");

        // 时间信息
        String timeInfo = formatTimeInfo(activity.getBeginTime(), activity.getFinishTime());
        sb.append("**时间**：").append(timeInfo).append("\n\n");

        // 地点信息
        if (activity.getPlacename() != null && !activity.getPlacename().isEmpty()) {
            sb.append("**场地**：").append(escapeMarkdown(activity.getPlacename())).append("\n\n");
        }
        if (activity.getAddress() != null && !activity.getAddress().isEmpty()) {
            sb.append("**地址**：").append(escapeMarkdown(activity.getAddress())).append("\n\n");
        }

        // 场地图
        if (activity.getCoverUrl() != null && !activity.getCoverUrl().isEmpty()) {
            sb.append("**场地图**:\n\n");
            sb.append("![场地图](").append(activity.getCoverUrl()).append(")\n\n");
        }

        // 参与人数
        sb.append("**参与人数**: ").append(activity.getParticipantReal())
                .append(" / ").append(activity.getParticipantMax()).append("\n\n");

        // 参与者列表
        if (activity.getParticipants() != null && !activity.getParticipants().isEmpty()) {
            sb.append("#### 参与者\n\n");
            for (Participant p : activity.getParticipants()) {
                sb.append(buildParticipantLine(p));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 构建参与者信息行
     */
    private static String buildParticipantLine(Participant p) {
        StringBuilder sb = new StringBuilder();

        sb.append("- **").append(escapeMarkdown(p.getName())).append("**");

        // 性别图标
        String genderIcon = getGenderIcon(p.getGender());
        if (genderIcon != null && !genderIcon.isEmpty()) {
            sb.append(" ").append(genderIcon);
        }

        // UTR 和等级
        if (p.getSinglesUtr() != null && p.getSinglesUtr() > 0) {
            sb.append(" UTR:").append(String.format("%.2f", p.getSinglesUtr()));
        }
        if (p.getTennisLevel() != null && !p.getTennisLevel().isEmpty()) {
            sb.append(" 等级:").append(p.getTennisLevel());
        }

        // 头像
        if (p.getAvatar() != null && !p.getAvatar().isEmpty()) {
            sb.append("\n  ");
            sb.append("![头像](").append(p.getAvatar()).append(")");
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * 格式化时间信息，包含星期几
     */
    private static String formatTimeInfo(String beginTime, String finishTime) {
        if (beginTime == null || beginTime.isEmpty()) {
            return "时间待定";
        }

        try {
            LocalDateTime begin = LocalDateTime.parse(beginTime, DATETIME_FORMATTER);
            String beginStr = begin.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

            // 计算星期几
            String weekday = WEEKDAY_MAP[begin.getDayOfWeek().getValue()];

            if (finishTime != null && !finishTime.isEmpty()) {
                LocalDateTime finish = LocalDateTime.parse(finishTime, DATETIME_FORMATTER);
                String finishStr = finish.format(DateTimeFormatter.ofPattern("HH:mm"));
                return String.format("%s %s ~ %s", beginStr, weekday, finishStr);
            }

            return beginStr + " " + weekday;
        } catch (Exception e) {
            log.warn("解析时间失败：beginTime={}, finishTime={}", beginTime, finishTime, e);
            return beginTime + (finishTime != null ? " ~ " + finishTime : "");
        }
    }

    /**
     * 获取性别图标
     */
    private static String getGenderIcon(Integer gender) {
        if (gender == null) {
            return "";
        }
        return switch (gender) {
            case 1 -> "♂";
            case 2 -> "♀";
            default -> "";
        };
    }

    /**
     * 转义 markdown 特殊字符
     */
    private static String escapeMarkdown(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // 转义可能的 markdown 特殊字符
        return text.replace("#", "\\#")
                .replace("*", "\\*")
                .replace("_", "\\_")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }
}
