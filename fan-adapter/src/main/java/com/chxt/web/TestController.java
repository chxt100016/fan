package com.chxt.web;


import com.chxt.client.bluebubbles.BlueBubblesClient;
import com.chxt.job.DongYaJob;
import com.chxt.tennis.AtpCollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

    @Resource
    private BlueBubblesClient blueBubblesClient;

    @Resource
    private DongYaJob dongYaJob;


    @RequestMapping("/dongYa")
    public void dongYa() {
        this.dongYaJob.monitorTennisMatches();
    }

    @RequestMapping("/blueBubbles")
    public void blueBubbles(@RequestParam("msg") String msg) {
        this.blueBubblesClient.send(msg);
    }


}
