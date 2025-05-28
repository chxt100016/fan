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

import jakarta.annotation.Resource;
import lombok.SneakyThrows;

@Service
public class GateNoticeService {

    private static final String GATE_STREAM = "gate";
    
    private static final String RTSP_URL = "rtsp://admin:IZOGRT@192.168.1.239:554/h264/ch1/main/av_stream";

    private static final long[] INTERVAL = {2000, 5000, 10000, 0};

    @Resource
    private PictureStreamCache pictureStreamCache;

    @Resource
    private WechatWorkClient wechatWorkClient;

    @SneakyThrows
    public void touch() {
        List<byte[]> images = new ArrayList<>();
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(RTSP_URL);
        grabber.setOption("rtsp_transport", "tcp");
        Java2DFrameConverter converter = new Java2DFrameConverter();
        Long timeStamp = 0L;

        for (int i = 0; i < 4; i++) {
            Frame frame = grabber.grabImage();
            // 丢弃老的帧
            do {
                frame = grabber.grabImage();
                if (frame == null) {
                    throw new RuntimeException("获取图片失败");
                }
            } while (frame.timestamp < timeStamp);

            BufferedImage bufferedImage = converter.convert(frame);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", baos);
            images.add(baos.toByteArray());
            timeStamp = frame.timestamp + INTERVAL[i]; // 间隔
            TimeUnit.MILLISECONDS.sleep(INTERVAL[i]); // 间隔
        }

        converter.close();
        grabber.stop();
        grabber.release();

        // 生成唯一ID，使用时间戳作为标识
        String uniqueId = String.valueOf(System.currentTimeMillis());

        byte[] cover = ThumbnailUtils.generate(images, 1);

        // 更新图片流
        pictureStreamCache.getPictureStream(GATE_STREAM).update(uniqueId, cover, images);

        // 微信通知
        String imageId = this.wechatWorkClient.uploadImg(uniqueId, cover, TokenFactory.innerStore(TokenEnum.WECHAT_WORK_ALARM));
        this.wechatWorkClient.appImage(imageId, TokenFactory.innerStore(TokenEnum.WECHAT_WORK_ALARM));
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
} 