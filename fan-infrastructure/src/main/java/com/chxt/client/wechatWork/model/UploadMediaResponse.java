package com.chxt.client.wechatWork.model;

import lombok.Data;

@Data
public class UploadMediaResponse {

    private Integer errcode;

    private String errmsg;

    private String type;

    private String mediaId;
}
