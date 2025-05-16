package com.chxt.notice;

import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.chxt.cache.PictureStreamCache;
import com.chxt.client.huanglong.HuanglongClient;
import com.chxt.client.huanglong.TennisCourt;
import com.chxt.domain.pic.DayTable;
import com.chxt.domain.pic.TimeCell;
import com.chxt.domain.stream.PictureStream;

@Service
public class TennisNoticeService {

    private static final String STREAM_NAME = "tennis";

    @Resource
    private PictureStreamCache pictureStreamCache;

    @Resource
    private HuanglongClient huanglongClient;

    public void touch() {
        List<TennisCourt> now = huanglongClient.getOutdoorAndIndoor(5);
        TennisCourtSelector selector = new TennisCourtSelector();

        List<TennisCourt> targetCourt = selector.getTargetCourt(now , Collections.emptyList());
        if (CollectionUtils.isEmpty(targetCourt)) {
            return;
        }

        List<TimeCell> timeTables = targetCourt.stream()
                .map(item -> new TimeCell(item.getDate(), item.getTimetableEnum()))
                .collect(Collectors.toList());

        byte[] bytes = new DayTable().getByte(timeTables);
        pictureStreamCache.getPictureStream(STREAM_NAME).update(Collections.singletonList(bytes));

    }

    public byte[] getStilImage() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(STREAM_NAME);
        return pictureStream.getStillImage();
    }


    public boolean check() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(STREAM_NAME);
        return pictureStream.check();
    }

    public void streamMjpeg(OutputStream outputStream) {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(STREAM_NAME);
        pictureStream.stream(outputStream, 1000);
    }
}
