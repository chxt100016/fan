package com.chxt.web;

import com.chxt.tennis.AtpCollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/tennis/collect")
public class TennisCollectController {

    @Resource
    private AtpCollectService atpCollectService;

    @GetMapping("/tournaments")
    public String tournaments(@RequestParam("year") Integer year) {
        int size = atpCollectService.tournaments(year);
        return "赛事API采集完成, 数量=" + size;
    }



    @GetMapping("/currentDraws")
    public String collectCurrentDraws() {
        atpCollectService.currentDraws();
        return "当前签表采集完成";
    }

    @GetMapping("/draws")
    public String draws(@RequestParam("tournamentId") String tournamentId, @RequestParam("year") int year) {
        atpCollectService.draws(tournamentId, year);
        return "签表采集完成: " + tournamentId + "/" + year;
    }

    @GetMapping("/currentMatch")
    public String collectCurrentMatch() {
        atpCollectService.currentMatch();
        return "比赛详情采集完成";
    }
}
