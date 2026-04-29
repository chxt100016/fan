package com.chxt.domain.notice;

import com.chxt.domain.notice.model.NoticeContext;
import com.chxt.domain.notice.model.NoticeProviderEnum;
import com.chxt.domain.notice.model.ScreenEnum;
import com.chxt.domain.notice.provider.NoticeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NoticeManager {

    private final Map<NoticeProviderEnum, NoticeProvider> providerMap;

    @Autowired
    public NoticeManager(List<NoticeProvider> providers) {
        this.providerMap = providers.stream().collect(Collectors.toMap(NoticeProvider::getName, Function.identity()));
    }


    public void notice(ScreenEnum screen, Object data) {
        NoticeProvider noticeProvider = this.providerMap.get(screen.getProviderType());
        if (noticeProvider == null) {
            throw new IllegalArgumentException("未找到通知提供者: " + screen.getProviderType());
        }
        NoticeContext context = new NoticeContext().setData(data).setScreenEnum(screen);
        noticeProvider.notice(context);
    }


}
