package com.chxt.domain.notice.gateway;

import org.springframework.stereotype.Component;

@Component
public interface BlueBubblesGateway {

    void send(String message);

    void send(String guid, String message);
}
