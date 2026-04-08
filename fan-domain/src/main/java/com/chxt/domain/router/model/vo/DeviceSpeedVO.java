package com.chxt.domain.router.model.vo;

import lombok.Data;

@Data
public class DeviceSpeedVO {
    private String mac;
    private String ip;
    private String name;
    private String downloadSpeed;
    private String uploadSpeed;
    private Long bytesReceived;
    private Long bytesSend;
}
