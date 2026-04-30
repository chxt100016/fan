package com.chxt.web;


import com.chxt.tennis.AtpCollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {


    @Resource
    private AtpCollectService atpCollectService;

    @RequestMapping("/tournaments")
    public void tournaments() {
        this.atpCollectService.tournaments();
    }

    @RequestMapping("/currentDraws")
    public void currentDraws() {
        this.atpCollectService.currentDraws();
    }


}
