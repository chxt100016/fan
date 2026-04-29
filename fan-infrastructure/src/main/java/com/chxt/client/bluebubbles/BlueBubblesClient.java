package com.chxt.client.bluebubbles;

import com.chxt.domain.notice.gateway.BlueBubblesGateway;
import com.chxt.domain.utils.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BlueBubblesClient implements BlueBubblesGateway {

    private static final String URL = "http://192.168.1.10:1234/api/v1/message/text";

    private static final String PASSWORD = "123456Aa."; // BlueBubbles 服务端配置的密码

    private static final String CHAT_GUID = "any;-;+8613372507785"; // 目标对话的 GUID

    public void send(String message) {
        Http.uri(URL)
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

    public void send(String guid, String message) {
        String res = Http.uri(URL)
                .param("password", PASSWORD)
                .entity("chatGuid", guid)
                .entity("tempGuid", String.valueOf(System.currentTimeMillis()))
                .entity("message", message)
                .entity("method", "apple-script")
                .entity("subject", "")
                .entity("effectId", "")
                .entity("selectedMessageGuid", "")
                .doPost().result();
        log.info(res);
    }
}