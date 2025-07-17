package com.chxt.cache.token;

import com.chxt.cache.token.model.TokenHandlerParam;
import com.chxt.cache.token.handler.AqaraTokenHandler; 
import com.chxt.cache.token.handler.EzvizTokenHandler;
import com.chxt.cache.token.handler.WechatWorkTokenHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenEnum {
    AQARA(TokenHandlerParam.builder()
            .appId("10095368717699645445d6ed")
            .appKey("2zfrsmm0ffawgz7tqbq5elcctd4glmuv")
            .keyId("K.1009536872050982912")
            .username("546555918@qq.com")
            .password("123456Aa.").build(),
            "aqara账号token", AqaraTokenHandler.class),

    EZVIZ(TokenHandlerParam.builder()
            .appKey("a513af2d032844b99289a0d6a970e156")
            .appSecret("36c42ea614b1737166def27393685166").build(),
            "ezviz账号token", EzvizTokenHandler.class),

    WECHAT_WORK_ALARM(TokenHandlerParam.builder()
            .appId("ww664b7f560125a130")
            .appKey("1000002")
            .appSecret("rqO_GZfH4LQA5zT5C9TtDxCdstOE2FHNwJBU98Q-EXI").build(),
            "企业微信通知应用token", WechatWorkTokenHandler.class),

    WECHAT_WORK_TENNIS(TokenHandlerParam.builder()
            .appId("ww664b7f560125a130")
            .appKey("1000003")
            .appSecret("ljXcxGcLIjsIGgmtFDLplquNJG9VRVzk0i187y0edLs").build(),
            "企业微信网球应用token", WechatWorkTokenHandler.class),

    WECHAT_WORK_BELL(TokenHandlerParam.builder()
            .appId("ww664b7f560125a130")
            .appKey("1000007")
            .appSecret("KxpzakJF3ycUYEVebOhiExitz5RZmUI3qw45OT7GRAk").build(),
            "企业微信贝儿应用token", WechatWorkTokenHandler.class);



    private final TokenHandlerParam param;
    private final String desc;
    private final Class<?> handler;

}
