package com.chxt.notice;

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

    
    private PictureStream pictureStream;

    private TennisCourtKeeper keeper;

    @PostConstruct
    public void init(){
        this.pictureStream = new PictureStream("TENNIS");
        this.keeper = new TennisCourtKeeper(this.pictureStream);

    }

    @Resource
    private HuanglongClient huanglongClient;

    public void touch() {
        List<TennisCourt> all = huanglongClient.getOutdoorAndIndoor(DAY_RANGE);
        keeper.add(all);
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
