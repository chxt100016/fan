package com.chxt.db.transaction.entity;

import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskPO {

    @TableId(type = IdType.AUTO)
    private String id;

    private String taskId;

    private String status;

    private Date data;

}
