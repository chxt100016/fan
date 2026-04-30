package com.chxt.notice;

import com.chxt.cache.stream.PictureStreamCache;
import com.chxt.client.huanglong.HuanglongClient;
import com.chxt.domain.booking.TennisBookingKeeper;
import com.chxt.domain.booking.TennisCourt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@Service
public class TennisNoticeService {
    
    private static final Integer DAY_RANGE = 4;

    private static final String TENNIS_STREAM = "tennis";

    private TennisBookingKeeper keeper;

    @Resource
    private PictureStreamCache pictureStreamCache;

    @PostConstruct
    public void init(){
        this.keeper = new TennisBookingKeeper();
    }

    @Resource
    private HuanglongClient huanglongClient;

    public void touch() {
        List<TennisCourt> all = huanglongClient.getOutdoorAndIndoor(DAY_RANGE);
        byte[] pic = keeper.add(all);
        if (pic != null && pic.length > 0) {
            // 更新图片流
            pictureStreamCache.getPictureStream(TENNIS_STREAM).update(pic, List.of(pic));
        }
    }

    public byte[] getStillImage() {
        return this.pictureStreamCache.getPictureStream(TENNIS_STREAM).getCover();
    }


    public boolean check() {

        return this.pictureStreamCache.getPictureStream(TENNIS_STREAM).check();
    }

    public void streamMjpeg(OutputStream outputStream) {
        this.pictureStreamCache.getPictureStream(TENNIS_STREAM).stream(outputStream, 3000, 15);
    }
}
