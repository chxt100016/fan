package com.chxt.web;


import com.chxt.client.bluebubbles.BlueBubblesClient;
import com.chxt.domain.dongya.ActivityMonitorService;

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
    private ActivityMonitorService activityMonitorService;


    @RequestMapping("/dongYa")
    public void dongYa() {
        this.activityMonitorService.monitorActivities();
    }

    @RequestMapping("/blueBubbles")
    public void blueBubbles(@RequestParam("msg") String msg) {
        this.blueBubblesClient.send(msg);
    }


}
