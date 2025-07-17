package com.chxt.client.aqara.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTokenParam{

    private String authCode;
    private String account;
    private Integer accountType;
}
