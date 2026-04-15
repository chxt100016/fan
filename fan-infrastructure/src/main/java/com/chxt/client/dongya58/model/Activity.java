package com.chxt.client.dongya58.model;

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
}
