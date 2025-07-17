package com.chxt.web.notice;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chxt.notice.GateNoticeService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

@RequestMapping("/notice/gate")
@RestController
public class GateNoticeController {

    @Resource
    private GateNoticeService gateService;

    @GetMapping("/touch")
    public String touch() {
        this.gateService.touch();
        return "ok";
    }

    @GetMapping("/check")
    public boolean check() {
        return this.gateService.check();
    }

    @GetMapping("/image")
    @SneakyThrows
    public void image(HttpServletResponse response) {
        byte[] image = this.gateService.getStilImage();
        response.getOutputStream().write(image);
    }

    @GetMapping(value = "/stream", produces = "multipart/x-mixed-replace;boundary=frame")
    public void streamMjpeg(HttpServletResponse response) throws IOException {
        response.setContentType("multipart/x-mixed-replace;boundary=frame");
        this.gateService.streamMjpeg(response.getOutputStream());
    }
}
