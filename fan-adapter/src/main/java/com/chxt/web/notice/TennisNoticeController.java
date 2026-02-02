package com.chxt.web.notice;

import java.io.IOException;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chxt.notice.TennisNoticeService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

@RequestMapping("/notice/tennis")
@RestController
public class TennisNoticeController {

    @Resource
    private TennisNoticeService tennisService;

    @GetMapping("/touch")
    public String touch() {
        this.tennisService.touch();
        return "ok";
    }

    @GetMapping("/check")
    public boolean check(){
        return this.tennisService.check();
    }


    @GetMapping("/image")
    @SneakyThrows
    public void image(HttpServletResponse response) {
        byte[] image = this.tennisService.getStillImage();
        response.getOutputStream().write(image);
    } 

    @GetMapping(value = "/stream", produces = "multipart/x-mixed-replace;boundary=frame")
    public void streamMjpeg(HttpServletResponse response) throws IOException {
        response.setContentType("multipart/x-mixed-replace;boundary=frame");
        this.tennisService.streamMjpeg(response.getOutputStream());
    }
  

}
