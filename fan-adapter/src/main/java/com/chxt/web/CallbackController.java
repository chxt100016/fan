package com.chxt.fantasticmonkey.controller;



import com.chxt.fantasticmonkey.util.wechatWork.AesException;
import com.chxt.fantasticmonkey.util.wechatWork.WXBizJsonMsgCrypt;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/callback")
@RestController
public class CallbackController {

    @RequestMapping("/wechatWork/verify")
    public String wechatWork(@RequestParam("msg_signature") String msgSignature,
                             @RequestParam("timestamp")String timestamp,
                             @RequestParam("nonce")String nonce,
                             @RequestParam("echostr")String echostr) throws AesException {
        WXBizJsonMsgCrypt crypt = new WXBizJsonMsgCrypt("r83eXsFkSGaa0OH47KuyuQIXV", "zY3oruAAHzAykzzaUUWKJ5h7UEGeRkkHbyCLEiGZO1s", "ww664b7f560125a130");
        String res = crypt.VerifyURL(msgSignature, timestamp, nonce, echostr);

        System.out.println(res);
        return res;
    }


}
