package com.chxt.client.ezviz;

import com.chxt.domain.utils.HttpOperator;
import com.chxt.client.ezviz.model.CaptureResponse;
import com.chxt.client.ezviz.model.EzvizToken;
import com.chxt.cache.token.model.TokenHandlerParam;
import com.chxt.cache.token.model.TokenItem;
import org.springframework.stereotype.Component;


@Component
public class EzvizClient {

    public CaptureResponse capture(String deviceSerial, TokenItem token) {
        return new HttpOperator()
                .uri("https://open.ys7.com/api/lapp/device/capture")
                .jsonHeader()
                .param("accessToken", token.getAccessToken())
                .param("deviceSerial", deviceSerial)
                .message("萤石开发平台-抓拍")
                .doPost()
                .result(CaptureResponse.class);
    }

    public void downloadImg(String uri, String path) {
        new HttpOperator()
                .uri(uri)
                .message("下载莹石告警图片")
                .doGet()
                .download(path);
    }

    public byte[] downloadImg(String uri) {
        return new HttpOperator()
                .uri(uri)
                .message("下载莹石告警图片")
                .doGet()
                .byteArray();
    }

    public TokenItem getToken(TokenHandlerParam param) {
        EzvizToken result = new HttpOperator()
                .uri("https://open.ys7.com/api/lapp/token/get")
                .jsonHeader()
                .param("appKey", param.getAppKey())
                .param("appSecret", param.getAppSecret())
                .doPost()
                .result(EzvizToken.class);

        return TokenItem.builder().accessToken(result.getData().getAccessToken()).expireTime(result.getData().getExpireTime()).build();
    }
}
