package com.chxt.db.transaction.entity;



import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("message_box")
@Data
@Accessors(chain = true)
public class MessageBoxPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String uniqueNo;

    private String userId;

    private String title;

    private String message;

    private String extraData;

    private String answer;

    private Date createTime;

    private Date updateTime;

}
