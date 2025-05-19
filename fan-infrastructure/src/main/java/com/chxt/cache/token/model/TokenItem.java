package com.chxt.cache.token.model;

import com.chxt.cache.token.TokenEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenItem {

    private TokenEnum tokenEnum;

    private String accessToken;

    private Long expireTime;

    private String refreshToken;
}
