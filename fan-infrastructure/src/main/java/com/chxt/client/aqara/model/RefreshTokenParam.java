package com.chxt.client.aqara.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenParam{

    private String refreshToken;

}
