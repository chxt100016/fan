package com.chxt.domain.transaction.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagTypeEnum {

    BASIC("BASIC", "基础标签"),

    AI("AI", "AI生成");



    private final String code;

    private final String name;
}
