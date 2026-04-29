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
@TableName("tennis_match_set")
public class TennisMatchSetPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String matchId;
    private Integer setNo;
    private Integer player1Games;
    private Integer player2Games;
    private String player1Points;
    private String player2Points;
    private Boolean tiebreak;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
