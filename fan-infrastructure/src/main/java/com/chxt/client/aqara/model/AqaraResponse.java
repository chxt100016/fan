package com.chxt.client.aqara.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AqaraResponse{

    private Integer code;
    private String requestId;
    private String message;
    private String result;
}
