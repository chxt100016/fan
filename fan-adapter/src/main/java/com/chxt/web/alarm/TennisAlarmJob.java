package com.chxt.fantasticmonkey.controller.alarm;

import com.chxt.fantasticmonkey.service.alarm.TennisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;


@Configuration
@EnableScheduling
@Slf4j
public class TennisAlarmJob {

    @Resource
    private TennisService tennisService;


    @Scheduled(cron = "0 */4 * * * ?")
    public void sendBookInfo() {
        log.info("黄龙室外网球场查询job - 开始");
        this.tennisService.BookInfoQueryJob();
        log.info("黄龙室外网球场查询job - 结束");
    }






}
