package com.chxt.db.tennis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tennis_player")
public class TennisPlayerPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String playerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationality;
    private String countryCode;
    @TableField("`rank`")
    private Integer rank;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
