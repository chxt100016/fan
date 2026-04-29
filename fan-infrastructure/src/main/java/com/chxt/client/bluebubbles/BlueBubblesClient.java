package com.chxt.client.bluebubbles;

import com.chxt.config.BlueBubblesConfig;

import com.chxt.domain.utils.Http;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.chxt.domain.notice.gateway.BlueBubblesGateway;

@Component
@Slf4j
@RequiredArgsConstructor
public class BlueBubblesClient implements BlueBubblesGateway{

    private final BlueBubblesConfig blueBubblesConfig;

    public void send(String message) {
        Http.uri(blueBubblesConfig.getUrl())
                .param("password", blueBubblesConfig.getPassword())
                .entity("chatGuid", blueBubblesConfig.getChatGuid())
                .entity("tempGuid", String.valueOf(System.currentTimeMillis()))
                .entity("message", message)
                .entity("method", "apple-script")
                .entity("subject", "")
                .entity("effectId", "")
                .entity("selectedMessageGuid", "")
                .doPost();
    }

    public void send(String guid, String message) {
        Http.uri(blueBubblesConfig.getUrl())
                .param("password", blueBubblesConfig.getPassword())
                .entity("chatGuid", guid)
                .entity("tempGuid", String.valueOf(System.currentTimeMillis()))
                .entity("message", message)
                .entity("method", "apple-script")
                .entity("subject", "")
                .entity("effectId", "")
                .entity("selectedMessageGuid", "")
                .doPost().result();

    }
}