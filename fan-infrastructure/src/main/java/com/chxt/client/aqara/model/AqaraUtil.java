package com.chxt.client.aqara.model;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AqaraUtil {

    public static Map<String, String> getHeader(String accessToken, String appId, String keyId, String appKey) {
        String time = System.currentTimeMillis() + "";
        HashMap<String, String> header = new HashMap<>();
        header.put("Appid", appId);
        header.put("Keyid", keyId);
        header.put("Nonce",  time);
        header.put("Time", time);
        String sign = AqaraUtil.createSign(accessToken, appId, keyId, time, time, appKey);
        header.put("Sign", sign);
        return header;
    }

    public static String createSign(String accessToken, String appId, String keyId, String nonce, String time, String appKey) {
        // 严格按照Accesstoken、Appid、Keyid、Nonce、Time
        // 顺序拼接为一个string串使用MD5生成签名值，将生成的签名值放在RequestHeader的Sign中；
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotBlank(accessToken)){
            sb.append("Accesstoken=").append(accessToken).append("&");
        }
        sb.append("Appid=").append(appId);
        sb.append("&").append("Keyid=").append(keyId);
        sb.append("&").append("Nonce=").append(nonce);
        sb.append("&").append("Time=").append(time).append(appKey);

        String signStr = sb.toString().toLowerCase();
        try {
            return MD5_32(signStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String MD5_32(String sourceStr) throws Exception {
        String result = "";

        try {
            byte[] b = md5(sourceStr.getBytes("UTF-8"));
            StringBuffer buf = new StringBuffer("");

            for(int offset = 0; offset < b.length; ++offset) {
                int i = b[offset];
                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }

            result = buf.toString();
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
        }

        return result;
    }

    private static byte[] md5(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        return md.digest();
    }
}
