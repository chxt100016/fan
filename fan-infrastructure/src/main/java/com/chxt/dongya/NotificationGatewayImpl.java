package com.chxt.dongya;

import com.chxt.client.bluebubbles.BlueBubblesClient;
import com.chxt.domain.dongya.gateway.NotificationGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationGatewayImpl implements NotificationGateway {

    @Resource
    private BlueBubblesClient blueBubblesClient;

    @Override
    public void sendNotification(String message) {
        try {
            blueBubblesClient.send(message);
            log.info("发送通知成功");
        } catch (Exception e) {
            log.error("发送通知失败", e);
        }
    }
}
