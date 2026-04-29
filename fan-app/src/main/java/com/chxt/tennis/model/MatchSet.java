package com.chxt.tennis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchSet {
    private String matchId;
    private Integer setNo;
    private Integer player1Games;
    private Integer player2Games;
    private String player1Points;
    private String player2Points;
    private Boolean tiebreak;
}
