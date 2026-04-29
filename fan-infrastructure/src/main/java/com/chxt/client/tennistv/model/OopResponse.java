package com.chxt.client.tennistv.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OopResponse {

    private Integer id;
    private Integer year;
    private String name;
    private String gender;
    private String start;
    private String end;
    private String type;
    private String location;
    private String surface;
    private InfoDetail info;
    private List<OopDay> oop;

    @Data
    public static class InfoDetail {
        private String title;
        private String city;
        private String inOutdoor;
        private Double utcOffset;
        private String qualStart;
        private String mainStart;
        private Integer drawSizeSM;
        private Integer drawSizeDM;
        private Integer drawSizeSQ;
        private Integer drawSizeDQ;
        private String commitment;
        private String prize;
        private String tdiId;
    }

    @Data
    public static class OopDay {
        private Integer DateSeq;
        @JSONField(name = "Courts")
        private Map<String, CourtDetail> courts;
    }

    @Data
    public static class CourtDetail {
        private Integer CourtId;
        private String CourtName;
        private List<MatchDetail> Matches;
    }

    @Data
    public static class MatchDetail {
        private String MatchId;
        private String AssociationCode;
        private String UmpireFirstName;
        private String UmpireLastName;
        private Integer DateSeq;
        private String MatchDate;
        private Integer CourtId;
        private String CourtName;
        private RoundInfo Round;
        private String MatchTime;
        private Integer NumberOfSets;
        private String Status;
        private String WinningPlayerId;
        private String Serve;
        private PlayerTeam PlayerTeam1;
        private PlayerTeam PlayerTeam2;
        private Integer TournamentId;
        private Integer TournamentYear;
        private String PulseStatus;
        private Integer CourtSeq;
        private String onDemandUrl;
        private String NotBeforeISOTime;
        private String NotBeforeText;
    }

    @Data
    public static class RoundInfo {
        private String LongName;
    }

    @Data
    public static class PlayerTeam {
        private String PlayerId;
        private String PlayerFirstName;
        private String PlayerFirstNameFull;
        private String PlayerLastName;
        private String PlayerCountryCode;
        private String SeedPlayerTeam;
        private List<SetScore> Sets;
    }

    @Data
    public static class SetScore {
        private Integer SetNumber;
        private String SetScore;
    }
}
