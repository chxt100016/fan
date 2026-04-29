package com.chxt.tennis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private String matchId;
    private String tournamentId;
    private String round;
    private String roundName;
    private String drawType;
    private String playerId1;
    private String playerId2;
    private String playerName1;
    private String playerName2;
    private String score;
    private String setsScore;
    private String status;
    private String winnerId;
    private String courtName;
    private LocalDateTime notBeforeTime;
    private String notBeforeText;
    private LocalDateTime matchTime;
    private String source;
    private List<MatchSet> sets;
}
