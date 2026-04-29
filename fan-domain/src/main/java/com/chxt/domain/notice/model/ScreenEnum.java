package com.chxt.domain.notice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public enum ScreenEnum {

    GATE("gate", "大门", NoticeProviderEnum.HOMEKIT,
            Map.of("key", "gate")
    ),
    TENNIS("tennis", "网球", NoticeProviderEnum.HOMEKIT,
            Map.of("key", "tennis")
    ),
    DONG_YA("dong_ya", "动鸭", NoticeProviderEnum.I_MESSAGE,
            Map.of("guid", "any;-;+8613372507785")
    )
    ;


    private final String code;

    private final String name;

    private final NoticeProviderEnum providerType;

    private final Map<String, String> config;
}
