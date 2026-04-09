package com.chxt.client.bluebubbles;

import org.springframework.stereotype.Component;

import com.chxt.domain.utils.Http;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BlueBubblesClient {

    private static final String URL = "http://localhost:1234/api/v1/message/text";
//    private static final String URL = "http://host.docker.internal:1234/api/v1/message/text";
    private static final String PASSWORD = "123456Aa."; // BlueBubbles 服务端配置的密码
    private static final String CHAT_GUID = "iMessage;-;+8613372507785"; // 目标对话的 GUID

    public void send(String message) {
        Http
                .uri(URL)
                .param("password", PASSWORD)
                .entity("chatGuid", CHAT_GUID)
                .entity("tempGuid", String.valueOf(System.currentTimeMillis()))
                .entity("message", message)
                .entity("method", "apple-script")
                .entity("subject", "")
                .entity("effectId", "")
                .entity("selectedMessageGuid", "")
                .doPost();
    }


}