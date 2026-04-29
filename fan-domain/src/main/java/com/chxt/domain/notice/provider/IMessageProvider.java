package com.chxt.domain.notice.provider;

import com.chxt.domain.notice.gateway.BlueBubblesGateway;
import com.chxt.domain.notice.model.NoticeContext;
import com.chxt.domain.notice.model.NoticeProviderEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class IMessageProvider implements NoticeProvider{

    @Resource
    private BlueBubblesGateway blueBubblesGateway;

    @Override
    public void notice(NoticeContext noticeContext) {
        String guid = noticeContext.getScreenEnum().getConfig().get("guid");
        String msg = noticeContext.getData().toString();
        this.blueBubblesGateway.send(guid, msg);
    }

    @Override
    public NoticeProviderEnum getName() {
        return NoticeProviderEnum.I_MESSAGE;
    }
}
