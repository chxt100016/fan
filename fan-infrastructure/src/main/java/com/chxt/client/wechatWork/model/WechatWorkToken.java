package com.chxt.client.wechatWork.model;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatWorkToken {
    private String errcode;
    private String errmsg;
    private String accessToken;
    private Long expiresIn;
}
