package com.chxt.web.tennis;

import com.chxt.tennis.TennisCollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/tennis/collect")
public class TennisCollectController {

    @Resource
    private TennisCollectService tennisCollectService;

    @GetMapping("/tournaments")
    public void tournaments(@RequestParam("year") Integer year) {
        tennisCollectService.tournaments(year);
    }


    @GetMapping("/currentDraws")
    public String collectCurrentDraws() {
        tennisCollectService.currentDraws();
        return "当前签表采集完成";
    }

    @GetMapping("/draws")
    public String draws(@RequestParam("tour") String tour, @RequestParam("tournamentId") String tournamentId, @RequestParam("year") int year) {
        tennisCollectService.draws(tour, tournamentId, year);
        return "签表采集完成: " + tournamentId + "/" + year;
    }

    @GetMapping("/currentMatch")
    public String collectCurrentMatch() {
        tennisCollectService.currentMatch();
        return "比赛详情采集完成";
    }

    @GetMapping("/live")
    public void live() {
        tennisCollectService.liveMatch();
    }
}
