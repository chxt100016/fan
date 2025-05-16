package com.chxt.notice;

import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.chxt.cache.PictureStreamCache;
import com.chxt.client.huanglong.HuanglongClient;
import com.chxt.domain.pic.TimeCell;
import com.chxt.domain.pic.TimeTable;
import com.chxt.domain.stream.PictureStream;
import com.chxt.domain.tennis.TennisCourt;
import com.chxt.domain.utils.DateStandardUtils;

@Service
public class TennisNoticeService {

    private static final String TEENIS_STREAM = "tennis";

    @Resource
    private PictureStreamCache pictureStreamCache;

    @Resource
    private HuanglongClient huanglongClient;

    public void touch() {
        List<TennisCourt> tennisCourts = huanglongClient.getOutdoorAndIndoor(5);
        tennisCourts = tennisCourts.stream().filter(item -> item.getBookable()).collect(Collectors.toList());
        String allCountStr = tennisCourts.stream()
            .map(item -> item.getTimetableEnum().getCode() + ":" + DateStandardUtils.getDayHour(item.getDate()) + ":" + item.getFieldName())
            .collect(Collectors.joining(";"));
        String uniqueId = DigestUtils.md5Hex(allCountStr);
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);

        if (pictureStream.isSame(uniqueId)) {
            return;
        }

        List<TimeCell> timeTables = tennisCourts.stream()
                .map(item -> new TimeCell(item.getDate(), item.getTimetableEnum()))
                .collect(Collectors.toList());

        // byte[] bytes = new DayTable().getByte(timeTables);
        byte[] bytes = new TimeTable().getByte(timeTables);
        pictureStream.update(uniqueId, Collections.singletonList(bytes));

    }

    public byte[] getStilImage() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);
        return pictureStream.getStillImage();
    }


    public boolean check() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);
        return pictureStream.check();
    }

    public void streamMjpeg(OutputStream outputStream) {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);
        pictureStream.stream(outputStream, 1000);
    }
}
