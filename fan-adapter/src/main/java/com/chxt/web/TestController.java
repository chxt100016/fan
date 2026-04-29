package com.chxt.web;


import com.chxt.cache.token.TokenEnum;
import com.chxt.cache.token.TokenFactory;
import com.chxt.client.bluebubbles.BlueBubblesClient;
import com.chxt.client.ezviz.EzvizClient;
import com.chxt.client.ezviz.model.CaptureResponse;
import com.chxt.job.DongYaJob;
import com.chxt.job.TennisDataCollectJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;




@RestController
@Slf4j
public class TestController {

    @Resource
    private EzvizClient ezvizClient;

    @Resource
    private BlueBubblesClient blueBubblesClient;

    @Resource
    private DongYaJob dongYaJob;

    @Resource
    private TennisDataCollectJob tennisDataCollectJob;

    @RequestMapping("/tennisDateCollectSchedule")
    public void tennisDataCollectSchedule() {
        this.tennisDataCollectJob.collectLiveMatches();
    }

    @RequestMapping("/dongya")
    public String dongya() {
        dongYaJob.monitorTennisMatches();
        return "OK";
    }


    @RequestMapping(value = "/version", method=RequestMethod.GET)
    public String version() {
        return "1.0.21";
    }
    

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/bluebubbles")
    public String bluebubbles(@RequestParam("message") String message) {
        this.blueBubblesClient.send(message);
        return "success";
    }

    @GetMapping("/ezviz")
    public String ezviz() {
        CaptureResponse capture = this.ezvizClient.capture("G69552993", TokenFactory.innerStore(TokenEnum.EZVIZ));
        this.ezvizClient.downloadImg(capture.getData().getPicUrl(), "/Users/chenxintong/Downloads/1.jpeg");
        return "success";

    }

}
