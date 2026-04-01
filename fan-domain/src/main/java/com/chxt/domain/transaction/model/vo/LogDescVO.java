package com.chxt.domain.transaction.model.vo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data

public class LogDescVO {

    private Map<String, String> data;

    public LogDescVO() {
        this.data = new HashMap<>();
    }

    public LogDescVO(String str) {
        this.data = JSON.parseObject(str, new TypeReference<Map<String, String>>() {});
    }

    public LogDescVO put(String k1, String v1) {
        data.put(k1, v1);
        return this;
    }

    public String format() {
        return JSON.toJSONString(data);
    }



}
