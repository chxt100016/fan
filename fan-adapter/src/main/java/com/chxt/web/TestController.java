package com.chxt.web;


import com.chxt.tennis.AtpCollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@Slf4j
public class TestController {


    @Resource
    private AtpCollectService atpCollectService;

    @RequestMapping("/tennisDateCollectSchedule")
    public void tennisDataCollectSchedule() {
        this.atpCollectService.tournaments();
    }



}
