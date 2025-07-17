package com.chxt.db.transaction.entity;



import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("task")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskPO {

    @TableId(type = IdType.AUTO)
    private String id;

    private String taskId;

    private String status;

	private String remark;

    private String data;


}
