package com.chxt.notice;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.chxt.cache.stream.PictureStreamCache;
import com.chxt.cache.token.TokenEnum;
import com.chxt.cache.token.TokenFactory;
import com.chxt.client.ezviz.EzvizClient;
import com.chxt.client.ezviz.model.CaptureResponse;
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
        
        
        // // 拍5张照片并收集图片数据
        // for (int i = 0; i < 5; i++) {
        //     CaptureResponse response = this.ezvizClient.capture(DEVICE_SERIAL, TokenFactory.innerStore(TokenEnum.EZVIZ));
        //     byte[] image = this.ezvizClient.downloadImg(response.getData().getPicUrl());
        //     images.add(image);
        // }

        // 生成唯一ID，使用时间戳作为标识
        String uniqueId = DigestUtils.md5Hex(DEVICE_SERIAL + System.currentTimeMillis());
        
        byte [] a = Files.readAllBytes(Paths.get("/Users/chenxintong/Downloads/1.jpeg"));
        images.add(a);
        
        // 更新图片流
        pictureStreamCache.getPictureStream(GATE_STREAM).update(uniqueId, images);
    }

    public byte[] getStilImage() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(GATE_STREAM);
        return pictureStream.getStillImage();
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