package com.chxt.client.tennistv.model;

import lombok.Data;

import java.util.List;

@Data
public class DrawsResponse {
    private Draw MS;
    private Draw MD;
    private Draw WS;
    private Draw WD;

    @Data
    public static class Draw {
        private String EventTypeCode;
        private String Description;
        private Integer DrawSize;
        private Integer NumByes;
        private Boolean HasRoundRobin;
        private Boolean IsTeamEvent;
        private List<Round> Rounds;
    }

    @Data
    public static class Round {
        private Integer RoundId;
        private String RoundName;
        private List<Fixture> Fixtures;
    }

    @Data
    public static class Fixture {
        private String MatchCode;
        private ResultInfo Result;
        private DrawLine DrawLineTop;
        private DrawLine DrawLineBottom;
        private Boolean IsTopKnown;
        private Boolean IsBottomKnown;
        private Integer Winner;
        private String PulseStatus;
    }

    @Data
    public static class ResultInfo {
        private String MatchCode;
        private Integer Winner;
        private String ResultReason;
        private TeamInfo TeamTop;
        private TeamInfo TeamBottom;
    }

    @Data
    public static class TeamInfo {
        private PlayerInfo Player;
    }

    @Data
    public static class DrawLine {
        private Integer DrawLine;
        private Integer Seed;
        private List<PlayerInfo> Players;
    }

    @Data
    public static class PlayerInfo {
        private String PlayerId;
        private String FirstName;
        private String LastName;
        private String Nationality;
        private Integer OrderInTeam;
    }
}
