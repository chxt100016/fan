package com.chxt.web;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chxt.cache.token.TokenEnum;
import com.chxt.cache.token.TokenFactory;
import com.chxt.client.ezviz.EzvizClient;
import com.chxt.client.ezviz.model.CaptureResponse;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;




@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private EzvizClient ezvizClient;

    private String flag = "1";

    @RequestMapping(value = "/version", method=RequestMethod.GET)
    public String version() {
        return "1.0.21";
    }
    

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/ezviz")
    public String ezviz() {
        CaptureResponse capture = this.ezvizClient.capture("G69552993", TokenFactory.innerStore(TokenEnum.EZVIZ));
        this.ezvizClient.downloadImg(capture.getData().getPicUrl(), "/Users/chenxintong/Downloads/1.jpeg");
        return "success";

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
            try {
                byte[] imageBytes = getLatestImageFrame();
                writeFrameToResponse(response, imageBytes);
                Thread.sleep(1000); // 控制帧率为10 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    private void writeFrameToResponse(HttpServletResponse response, byte[] imageBytes) throws IOException {
        response.getOutputStream().write((
            "--frame\r\n" +
            "Content-Type: image/jpeg\r\n" +
            "Content-Length: " + imageBytes.length + "\r\n" +
            "\r\n").getBytes());
        response.getOutputStream().write(imageBytes);
        response.getOutputStream().write("\r\n".getBytes());
        response.getOutputStream().flush();
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
