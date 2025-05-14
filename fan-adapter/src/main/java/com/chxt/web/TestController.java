package com.chxt.fantasticmonkey.controller;

import com.chxt.fantasticmonkey.controller.job.DailyElectricityJob;
import com.chxt.fantasticmonkey.domain.token.TokenEnum;
import com.chxt.fantasticmonkey.domain.token.tokenStore.InnerTokenStore;
import com.chxt.fantasticmonkey.infrastructure.env.EnvRepository;
import com.chxt.fantasticmonkey.infrastructure.transmission.Transmission2Client;
import com.chxt.fantasticmonkey.model.property.TennisProperty;
import com.chxt.fantasticmonkey.model.token.TokenItem;
import com.chxt.fantasticmonkey.model.transmission.Torrents;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private InnerTokenStore innerTokenStoreComponent;


    @Resource
    private DailyElectricityJob dailyElectricityJob;

    @RequestMapping("/sleep")
    @SneakyThrows
    public String sleep(Integer num) {
        TimeUnit.SECONDS.sleep(num);
        return "wait " + num + " seconds";
    }

    @RequestMapping("ele")
    public void ele() {
        this.dailyElectricityJob.electricity();
    }

    @GetMapping("/hello")
    public String hellp(){
        return "world2";
    }
        
    
    


    @RequestMapping("/tokenStore")
    public void getTokenStore(HttpServletResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (TokenEnum value : TokenEnum.values()) {
            TokenItem token = this.innerTokenStoreComponent.getToken(value);
            response.getWriter().write(String.format("<div><h2>name:%s</h2><p>accessToken:%s</p><p>refreshToken:%s</p><p>expireTime:%s</p><div>", value.name(), token.getAccessToken(), token.getRefreshToken(), DateFormatUtils.format(new Date(token.getExpireTime()), "yyyy-MM-dd HH:mm:ss")));
            sb.append(String.format("name:%s, accessToken:%s, refreshToken:%s, expireTime:%s\n\n", value.name(), token.getAccessToken(), token.getRefreshToken(), DateFormatUtils.format(new Date(token.getExpireTime()), "yyyy-MM-dd HH:mm:ss")));

        }

    }

    @PostMapping("/ha")
    public String ph(@RequestBody String param){
        System.out.println(param);
        return param;
    }

    @GetMapping("/ha")
    public String gh(String param){
        System.out.println(param);
        return "{\"active\": \"false\"}";
    }

    @GetMapping("/error")
    public void error(HttpServletResponse response){
        response.setStatus(409);

    }

    @GetMapping("/s1")
    public void s1(HttpServletResponse response) throws InterruptedException {
        TimeUnit.SECONDS.sleep(100);

    }

    @GetMapping("/s2")
    public void s2(HttpServletResponse response) throws InterruptedException {
        TimeUnit.SECONDS.sleep(100);

    }





    @Resource
    private EnvRepository envRepository;

    @RequestMapping("/tennisProperty")
    public TennisProperty getTestProperty() {
        return this.envRepository.getTennisReport();

    }

    public static void main(String[] args) {
        Transmission2Client client = new Transmission2Client();

        for(int i = 0; i < 21; i ++) {
            new Thread(()-> {
                List<Torrents> torrents = client.get();
            }).start();
        }
//        new HttpOperator().uri("http://localhost:8888/test/error").doGet();
//        new HttpOperator().uri("http://2202.com:8888/test/ha").doGet();


    }
}
