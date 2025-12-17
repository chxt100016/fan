package com.chxt.db.transaction.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("user_mail")
public class UserMailPO {

    private Long id;

    private String userId;

    private String host;

    private String alias;

    private String username;

    private String password;

    private Date createTime;

    private Date updateTime;
}
