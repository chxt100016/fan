package com.chxt.notice;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.chxt.cache.stream.PictureStreamCache;
import com.chxt.client.huanglong.HuanglongClient;
import com.chxt.domain.pic.ScheduleImage;
import com.chxt.domain.pic.TimeCell;
import com.chxt.domain.pic.TimeTable;
import com.chxt.domain.stream.PictureStream;
import com.chxt.domain.tennis.TennisCourt;
import com.chxt.domain.tennis.TennisCourtSelector;


import jakarta.annotation.Resource;

@Service
public class TennisNoticeService {

    private static final String TEENIS_STREAM = "tennis";
    
    private static final Integer DAY_RANGE = 4;

    

    @Resource
    private PictureStreamCache pictureStreamCache;

    @Resource
    private HuanglongClient huanglongClient;

    public void touch() {
        List<TennisCourt> all = huanglongClient.getOutdoorAndIndoor(DAY_RANGE);

        // 根据时间限制，获取可预约的场地
        List<TennisCourt> available = TennisCourtSelector.getAvailable(all);
        if (CollectionUtils.isEmpty(available)) {
            return;
        }

        // 获取唯一标识 
        String uniqueId = TennisCourt.getUniqueString(available);
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);
        if (pictureStream.isSame(uniqueId)) {
            return;
        }

        List<TimeCell> timeTables = available.stream()
                .map(item -> new TimeCell(item.getDate(), item.getTimetableEnum()))
                .collect(Collectors.toList());

        byte[] a = new TimeTable().getByte(timeTables);

        Map<String,List<String>> dayOfWeekAndTimeMap = TennisCourt.getDayOfWeekAndTime(available);
        byte[] cover = new ScheduleImage(dayOfWeekAndTimeMap).generate();
        
        pictureStream.update(uniqueId, cover, Arrays.asList(a));

    }

    public byte[] getStilImage() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);
        return pictureStream.getCover();
    }


    public boolean check() {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);
        return pictureStream.check();
    }

    public void streamMjpeg(OutputStream outputStream) {
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);
        pictureStream.stream(outputStream, 3000, 15);
    }
}
