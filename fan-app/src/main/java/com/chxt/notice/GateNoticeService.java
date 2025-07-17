package com.chxt.notice;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import org.springframework.stereotype.Service;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import com.chxt.cache.stream.PictureStreamCache;
import com.chxt.cache.token.TokenEnum;
import com.chxt.cache.token.TokenFactory;

import com.chxt.client.wechatWork.WechatWorkClient;

import com.chxt.domain.stream.PictureStream;
import com.chxt.domain.utils.ThumbnailUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;


@Service
@Slf4j
public class GateNoticeService {

    private static final String GATE_STREAM = "gate";
    
    private static final String RTSP_URL = "rtsp://admin:IZOGRT@192.168.1.250:554/h264/ch1/main/av_stream";

    private static final long INTERVAL = TimeUnit.SECONDS.toMicros(3);

    @Resource
    private PictureStreamCache pictureStreamCache;

    @Resource
    private WechatWorkClient wechatWorkClient;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            // avutil.av_log_set_level(avutil.AV_LOG_QUIET);
        }).start();
    }

    @SneakyThrows
    public void touch() {
        
        
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(RTSP_URL);
        grabber.setOption("rtsp_transport", "tcp");
        grabber.setOption("fflags", "nobuffer");
        Java2DFrameConverter converter = new Java2DFrameConverter();
        grabber.start();
       
        List<byte[]> images = new ArrayList<>();
        try {
            images = capture(grabber, converter, images);
        } catch (Exception e) {
            log.error("capture error", e);
            throw e;
        } finally {
            grabber.close();
            converter.close();
        }
        if (images.size() < 4) {
            for (int i = images.size(); i < 4; i++) {
                images.add(new byte[0]);
            }
        }


        // 生成唯一ID，使用时间戳作为标识
        String uniqueId = String.valueOf(System.currentTimeMillis());

        byte[] cover = ThumbnailUtils.generate(images, 1);

        // 更新图片流
        pictureStreamCache.getPictureStream(GATE_STREAM).update(uniqueId, cover, images);

        // 微信通知
        String imageId = this.wechatWorkClient.uploadImg(uniqueId, cover, TokenFactory.innerStore(TokenEnum.WECHAT_WORK_ALARM));
        this.wechatWorkClient.appImage(imageId, TokenFactory.innerStore(TokenEnum.WECHAT_WORK_ALARM));
    }

    @SneakyThrows
    public List<byte[]> capture(FFmpegFrameGrabber grabber, Java2DFrameConverter converter, List<byte[]> images) {
        
        // 记录下一次允许拍照的时间戳
        long nextCaptureTime = 0;
        for (int i = 0; i < 4; i++) {
            log.debug("picture {}, nextCaptureTime: {}", i, nextCaptureTime);
            Frame frame = grabber.grabImage();
            // 持续获取帧直到找到符合时间要求的帧
            while (frame.timestamp < nextCaptureTime) {
                frame = grabber.grabImage();
            }


            BufferedImage bufferedImage = converter.convert(frame);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", baos);
            images.add(baos.toByteArray());
            // 更新下一次允许拍照的时间戳
            nextCaptureTime = frame.timestamp + INTERVAL;
        }
        return images;
    }

    public byte[] getStilImage() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(GATE_STREAM);
        return pictureStream.getCover();
    }

    public boolean check() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(GATE_STREAM);
        return pictureStream.check();
    }

    public void streamMjpeg(OutputStream outputStream) {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(GATE_STREAM);
        pictureStream.stream(outputStream, 2000, 15);
    }

    public static void main(String[] args) {
        String url = "https://public-image.pxb7.com/pxb7-upload/product/api/20250527/larker_332274a7-1aff-4415-9414-36b602952c44.jpg";
        String fileName = FilenameUtils.getName(url);
        System.out.println(fileName);
    }
} 