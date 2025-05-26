package com.chxt.notice;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import com.chxt.cache.stream.PictureStreamCache;

import com.chxt.client.ezviz.EzvizClient;

import com.chxt.domain.stream.PictureStream;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;

@Service
public class GateNoticeService {

    private static final String GATE_STREAM = "gate";
    private static final String DEVICE_SERIAL = "G69552993";

    @Resource
    private PictureStreamCache pictureStreamCache;

    @Resource
    private EzvizClient ezvizClient;

    @SneakyThrows
    public void touch() {
        List<byte[]> images = new ArrayList<>();
        String rtspUrl = "rtsp://admin:IZOGRT@192.168.1.239:554/h264/ch1/main/av_stream";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
        // 设置缓冲区大小
        grabber.setOption("buffer_size", "0");
        // 设置超时时间
        grabber.setOption("stimeout", "2000000");
        grabber.start();

        Java2DFrameConverter converter = new Java2DFrameConverter();

        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                // 重新连接流，确保获取最新的帧
                grabber.stop();
                grabber.start();
            }
            
            Frame frame = grabber.grabImage();
            if (frame != null) {
                BufferedImage bufferedImage = converter.convert(frame);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpeg", baos);
                images.add(baos.toByteArray());
            }
            Thread.sleep(1000); // 间隔1秒
        }

        grabber.stop();

        // 生成唯一ID，使用时间戳作为标识
        String uniqueId = DigestUtils.md5Hex(DEVICE_SERIAL + System.currentTimeMillis());

        // 更新图片流
        pictureStreamCache.getPictureStream(GATE_STREAM).update(uniqueId, images);
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