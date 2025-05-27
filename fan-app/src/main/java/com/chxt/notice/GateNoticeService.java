package com.chxt.notice;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.chxt.client.ezviz.EzvizClient;
import com.chxt.client.wechatWork.WechatWorkClient;
import com.chxt.domain.pic.ThumbnailPicture;
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
    private WechatWorkClient wechatWorkClient;

    @SneakyThrows
    public void touch() {
        List<byte[]> images = new ArrayList<>();
        String rtspUrl = "rtsp://admin:IZOGRT@192.168.1.239:554/h264/ch1/main/av_stream";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
        // 设置缓冲区大小
        grabber.setOption("buffer_size", "0");
        grabber.start();

        Java2DFrameConverter converter = new Java2DFrameConverter();
        TimeUnit.MILLISECONDS.sleep(100);

        for (int i = 0; i < 4; i++) {
            Frame frame = grabber.grabImage();
            if (frame != null) {
                BufferedImage bufferedImage = converter.convert(frame);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpeg", baos);
                images.add(baos.toByteArray());
            }
            Thread.sleep(2000); // 间隔
        }

        grabber.stop();

        // 生成唯一ID，使用时间戳作为标识
        String uniqueId = DigestUtils.md5Hex(DEVICE_SERIAL + System.currentTimeMillis());

        ThumbnailPicture thumbnailPicture = new ThumbnailPicture(images.get(0), images.get(1), images.get(2), images.get(3));
        byte[] cover = thumbnailPicture.generateThumbnail();

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