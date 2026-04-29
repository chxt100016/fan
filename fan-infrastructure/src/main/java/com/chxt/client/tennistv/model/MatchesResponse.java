package com.chxt.client.tennistv.model;

import lombok.Data;

import java.util.List;

@Data
public class MatchesResponse {
    private List<TournamentInfo> tournaments;
    private List<MatchInfo> matches;

    @Data
    public static class TournamentInfo {
        private String id;
        private Integer year;
        private String name;
        private String gender;
        private String start;
        private String end;
        private String type;
        private String location;
        private String surface;
    }

    @Data
    public static class MatchInfo {
        private String id;
        private String tournamentId;
        private String round;
        private String roundName;
        private PlayerInfo player1;
        private PlayerInfo player2;
        private String score;
        private String status;
        private String startTime;
    }

    @Data
    public static class PlayerInfo {
        private String id;
        private String firstName;
        private String lastName;
        private String fullName;
        private String nationality;
        private String countryCode;
    }
}
