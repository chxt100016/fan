package com.chxt.cache.token.handler;

import com.chxt.cache.token.model.TokenHandlerParam;
import com.chxt.cache.token.model.TokenItem;
import com.chxt.client.ezviz.EzvizClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class EzvizTokenHandler implements TokenHandler{

    @Resource
    private EzvizClient ezvizClient;

    @Override
    public TokenItem getToken(TokenHandlerParam param) {
        return this.ezvizClient.getToken(param);
    }

    @Override
    public TokenItem refreshToken(TokenHandlerParam param, TokenItem tokenItem) {
        return this.ezvizClient.getToken(param);
    }
}
