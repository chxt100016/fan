package com.chxt.client.wechatWork;



import com.chxt.cache.token.model.TokenHandlerParam;
import com.chxt.cache.token.model.TokenItem;
import com.chxt.client.wechatWork.model.SendMessageRequest;
import com.chxt.client.wechatWork.model.WechatWorkToken;
import com.chxt.domain.utils.HttpOperator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WechatWorkClient{

    public void appNews(List<Map<String, String>> articles, TokenItem token) {
        SendMessageRequest entity = SendMessageRequest.builder()
                .touser("@all")
                .msgtype("news")
                .agentid(token.getTokenEnum().getParam().getAppKey())
                .news(SendMessageRequest.SendMessageNews.builder().articles(articles).build())
                .build();

        new HttpOperator()
                .uri("https://qyapi.weixin.qq.com/cgi-bin/message/send")
                .param("access_token", token.getAccessToken())
                .entity(entity)
                .doPost();
    }

    public void appText(String content, TokenItem token) {
        SendMessageRequest entity = SendMessageRequest.builder()
                .touser("@all")
                .msgtype("text")
                .agentid(token.getTokenEnum().getParam().getAppKey())
                .text(SendMessageRequest.Text.builder().content(content).build())
                .build();

        new HttpOperator()
                .uri("https://qyapi.weixin.qq.com/cgi-bin/message/send")
                .param("access_token", token.getAccessToken())
                .entity(entity)
                .doPost();
    }

    public TokenItem getToken(TokenHandlerParam param) {
        WechatWorkToken wechatWorkToken = new HttpOperator()
                .uri("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
                .param("corpid", param.getAppId())
                .param("corpsecret", param.getAppSecret())
                .doPost()
                .result(WechatWorkToken.class);
        return TokenItem.builder().accessToken(wechatWorkToken.getAccessToken()).expireTime(new Date().getTime() + wechatWorkToken.getExpiresIn() * 1000).build();
    }
}
