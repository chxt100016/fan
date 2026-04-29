package com.chxt.domain.notice.provider;

import com.chxt.domain.notice.model.NoticeContext;
import com.chxt.domain.notice.model.NoticeProviderEnum;

public interface NoticeProvider {

    void notice(NoticeContext noticeContext);

    NoticeProviderEnum getName();
}
