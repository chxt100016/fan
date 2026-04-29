package com.chxt.domain.notice.provider;

import com.chxt.domain.notice.gateway.PictureStreamCacheGateway;
import com.chxt.domain.notice.model.NoticeContext;
import com.chxt.domain.notice.model.NoticeProviderEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HomeKitProvider implements NoticeProvider{

    @Resource
    private PictureStreamCacheGateway pictureStreamCacheGateway;

    @Override
    public void notice(NoticeContext noticeContext) {
        String key = noticeContext.getScreenEnum().getConfig().get("key");
        byte[] data = (byte[]) noticeContext.getData();
        pictureStreamCacheGateway.getPictureStream(key).update(List.of(data));
    }

    @Override
    public NoticeProviderEnum getName() {
        return NoticeProviderEnum.HOMEKIT;
    }
}
