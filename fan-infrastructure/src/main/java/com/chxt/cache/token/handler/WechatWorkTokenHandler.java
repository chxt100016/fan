package com.chxt.cache.token.handler;

import com.chxt.cache.token.model.TokenHandlerParam;
import com.chxt.cache.token.model.TokenItem;
import com.chxt.client.wechatWork.WechatWorkClient;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class WechatWorkTokenHandler implements TokenHandler{

    @Resource
    private WechatWorkClient wechatWorkClient;

    @Override
    public TokenItem getToken(TokenHandlerParam param) {
        return this.wechatWorkClient.getToken(param);
    }

    @Override
    public TokenItem refreshToken(TokenHandlerParam param, TokenItem tokenItem) {
        return this.wechatWorkClient.getToken(param);
    }
}
