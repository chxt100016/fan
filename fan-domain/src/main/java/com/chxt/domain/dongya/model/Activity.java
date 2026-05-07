package com.chxt.domain.dongya.model;

import com.chxt.domain.utils.DateStandardUtils;
import lombok.Data;

import java.util.List;

/**
 * 活动数据
 */
@Data
public class Activity {

    private Integer activityId;

    private String name;

    private String labels;

    private Integer type;

    private String beginTime;

    private String finishTime;

    private String province;

    private String city;

    private String district;

    private String address;

    private String fullAddress;

    private Double lng;

    private Double lat;

    private String placename;

    private Integer status;

    private String createdAt;

    private String updatedAt;

    private String coverUrl;

    private Integer creatorUserId;

    private Integer clubId;

    private Club club;

    private Integer participantMax;

    private Integer participantReal;

    private List<Participant> participants;

    private Boolean isJoined;

    private Integer tennisVerifiedType;

    private Integer ageGroup;

    private Integer sportType;

    private Integer matchType;

    private List<DivisionInfo> divisionInfo;

    @Data
    public static class Club {
        private Integer id;
        private String logo;
        private String name;
    }

    @Data
    public static class DivisionInfo {
        private Integer id;
        private Integer activityId;
        private String name;
        private Integer fee;
        private Integer maxPlayers;
        private Integer divisionFormat;
        private Integer sortOrder;
        private String levelTennis;
        private Integer partnerType;
        private Integer minTennisLevel;
        private Integer maxTennisLevel;
    }

    public String toMessage() {
        return "【新比赛】\n" +
                formatBasicInfo() + "\n" +
                formatParticipants();
    }

    private String formatBasicInfo() {
        StringBuilder sb = new StringBuilder();

        String timeInfo = DateStandardUtils.formatTimeInfo(this.beginTime, this.finishTime);
        int currentCount = this.participants != null ? this.participants.size() : 0;
        int maxCount = this.participantMax != null ? this.participantMax : 0;
        sb.append(this.divisionInfo.get(0).getLevelTennis()).append(" ").append(this.placename).append("\n");
        sb.append(timeInfo).append(" ").append(currentCount).append("/").append(maxCount).append("\n");
        return sb.toString();
    }

    private String formatParticipants() {
        if (this.participants == null || this.participants.isEmpty()) {
            return "【参与者】\n暂无参与者\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【参与者】\n");

        for (Participant p : this.participants) {
            sb.append("• ").append(formatParticipantDetail(p)).append("\n");
        }

        return sb.toString();
    }

    private String formatParticipantDetail(Participant p) {
        StringBuilder sb = new StringBuilder();
        sb.append(getGenderText(p.getGender())).append(" ");

        sb.append(" ").append(p.getName());

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
