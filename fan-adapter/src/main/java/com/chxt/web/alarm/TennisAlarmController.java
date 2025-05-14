package com.chxt.fantasticmonkey.controller.alarm;

import com.chxt.fantasticmonkey.service.alarm.TennisService;
import lombok.SneakyThrows;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/alarm/tennis")
public class TennisAlarmController {

    @Resource
    private TennisService tennisService;



    @GetMapping("/pic/latest")
    @SneakyThrows
    public void latest(HttpServletResponse response) {
        byte[] image = this.tennisService.getImage();
        response.getOutputStream().write(image);
    }

    @GetMapping("/query")
    public String query() {
        this.tennisService.BookInfoQuery();
        return "ok";
    }

    @GetMapping("/sensor")
    public boolean sensor(){
        return this.tennisService.getAlarmSensor();
    }

}
