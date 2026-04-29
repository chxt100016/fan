package com.chxt.db.tennis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tennis_match")
public class TennisMatchPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String matchId;
    private String tournamentId;
    private String round;
    private String roundName;
    private String drawType;
    private String player1Id;
    private String player2Id;
    private String player1Name;
    private String player2Name;
    private String score;
    private String setsScore;
    private String status;
    private String winnerId;
    private String courtName;
    private LocalDateTime notBeforeTime;
    private String notBeforeText;
    private LocalDateTime matchTime;
    private String source;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
