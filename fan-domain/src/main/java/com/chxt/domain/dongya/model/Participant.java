package com.chxt.domain.dongya.model;

import lombok.Data;

/**
 * 活动参与者
 */
@Data
public class Participant {

    private Integer id;

    private Integer activityId;

    private Integer divisionId;

    private Integer userId;

    private String name;

    private String mobile;

    private String birthdate;

    private Integer partnerParticipantId;

    private Integer partnerIdx;

    private String avatar;

    private Integer gender;

    private String description;

    private Double utr;

    private Double singlesUtr;

    private String tennisLevel;
}
