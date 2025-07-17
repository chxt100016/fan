package com.chxt.cache.token.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenHandlerParam {
    private String appId;
    private String appSecret;
    private String appKey;
    private String keyId;
    private String username;
    private String password;

}

