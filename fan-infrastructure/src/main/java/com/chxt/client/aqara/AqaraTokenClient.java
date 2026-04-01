package com.chxt.client.aqara;



import com.alibaba.fastjson2.JSONObject;
import com.chxt.domain.utils.Http;
import com.chxt.client.aqara.model.AuthCodeResult;
import com.chxt.client.aqara.model.TokenResponse;
import com.chxt.client.aqara.model.GetTokenParam;
import com.chxt.client.aqara.model.RefreshTokenParam;
import com.chxt.client.aqara.model.AqaraParam;
import com.chxt.client.aqara.model.AqaraResponse;
import com.chxt.client.aqara.model.AqaraUtil;
import com.chxt.client.aqara.model.AuthCodeParam;
import com.chxt.cache.token.model.TokenHandlerParam;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AqaraTokenClient {

    
    private final static String URI = "https://open-cn.aqara.com/v3.0/open/api";

    public AuthCodeResult getAuthCode(AuthCodeParam param, TokenHandlerParam tokenHandlerParam) {
        return this.handler("config.auth.getAuthCode", param, tokenHandlerParam, AuthCodeResult.class);

    }

    public TokenResponse getToken(GetTokenParam param, TokenHandlerParam tokenHandlerParam){
        return this.handler("config.auth.getToken", param, tokenHandlerParam, TokenResponse.class);
    }

    public TokenResponse refreshToken(RefreshTokenParam param, TokenHandlerParam tokenHandlerParam){
        return this.handler("config.auth.refreshToken", param, tokenHandlerParam, TokenResponse.class);
    }


    private <T> T handler(String intent, Object param, TokenHandlerParam tokenHandlerParam, Class<T> clazz){
        return this.handler(intent, param, tokenHandlerParam, null, clazz);
    }

    private <T> T handler(String intent, Object param, TokenHandlerParam tokenHandlerParam, String token, Class<T> clazz){
        AqaraParam entity = AqaraParam.builder().intent(intent).data(param).build();
        Map<String, String> header = AqaraUtil.getHeader(token, tokenHandlerParam.getAppId(), tokenHandlerParam.getKeyId(), tokenHandlerParam.getAppKey());
        AqaraResponse response = Http
                .uri(URI)
                .header(header)
                .jsonHeader()
                .entity(entity)
                .doPost()
                .result(AqaraResponse.class);

        return JSONObject.parseObject(response.getResult(), clazz);
    }




}
