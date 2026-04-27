package com.chxt.notice;

import com.chxt.cache.stream.PictureStreamCache;
import com.chxt.client.huanglong.HuanglongClient;
import com.chxt.domain.stream.PictureStream;
import com.chxt.domain.tennis.TennisCourt;
import com.chxt.domain.tennis.TennisCourtKeeper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@Service
public class TennisNoticeService {
    
    private static final Integer DAY_RANGE = 4;

    private static final String TENNIS_STREAM = "tennis";

    
    private PictureStream pictureStream;

    private TennisCourtKeeper keeper;

    @Resource
    private PictureStreamCache pictureStreamCache;

    @PostConstruct
    public void init(){
        this.pictureStream = new PictureStream("TENNIS");
        this.keeper = new TennisCourtKeeper();

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
        return this.pictureStream.getCover();
    }


    public boolean check() {

        return this.pictureStream.check();
    }

    public void streamMjpeg(OutputStream outputStream) {
        this.pictureStream.stream(outputStream, 3000, 15);
    }
}
