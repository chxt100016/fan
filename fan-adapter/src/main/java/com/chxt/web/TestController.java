package com.chxt.web;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestController {

    private String flag = "1";

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/flag")
    public String flag(String flag) {
        this.flag = flag;
        return "success";
    }

    @GetMapping(value = "/stream", produces = "multipart/x-mixed-replace;boundary=frame")
    public void streamMjpeg(HttpServletResponse response) throws IOException {
        response.setContentType("multipart/x-mixed-replace;boundary=frame");
        
        while (true) {
            // 1. 获取最新JPEG帧 (可以从摄像头、文件等获取)
            byte[] imageBytes = getLatestImageFrame();
            
            // 2. 写入MJPEG格式
            response.getOutputStream().write((
                "--frame\r\n" +
                "Content-Type: image/jpeg\r\n" +
                "Content-Length: " + imageBytes.length + "\r\n" +
                "\r\n").getBytes());
            response.getOutputStream().write(imageBytes);
            response.getOutputStream().write("\r\n".getBytes());
            response.getOutputStream().flush();
            
            // 3. 控制帧率
            try {
                Thread.sleep(1000); // 10 FPS
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    private byte[] getLatestImageFrame() {
        // 实现获取最新JPEG帧的逻辑
        // 可以从摄像头、文件系统或内存中获取
        try {
            String path = String.format("D:\\cover%s.png", flag);
            File file = new File(path);
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }

    }
}
