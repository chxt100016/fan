package com.chxt.db.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("mission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionPO {

    @TableId(type = IdType.AUTO)
    private String id;

    private String missionId;

    private String missionStatus;

    private String data;

}
