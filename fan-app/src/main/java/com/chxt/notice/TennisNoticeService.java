package com.chxt.notice;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.chxt.cache.stream.PictureStreamCache;
import com.chxt.client.huanglong.HuanglongClient;
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

    // 室外场地 工作日
    private static final List<Integer> OUT_DOOR_WEEKDAY_HOURS = Arrays.asList( 20, 21, 22);

    // 室外场地 周末
    private static final List<Integer> OUT_DOOR_WEEKEND_HOURS = Arrays.asList(18, 19, 20, 21, 22);

    // 室内场地 工作日
    private static final List<Integer> IN_DOOR_WEEKDAY_HOURS = Arrays.asList( 20, 21, 22);

    // 室内场地 周末
    private static final List<Integer> IN_DOOR_WEEKEND_HOURS = Arrays.asList(10,11,12,13,14,15,16, 17, 18, 19, 20, 21, 22);

    @Resource
    private PictureStreamCache pictureStreamCache;

    @Resource
    private HuanglongClient huanglongClient;

    public void touch() {
        List<TennisCourt> all = huanglongClient.getOutdoorAndIndoor(DAY_RANGE);

        // 根据时间限制，获取可预约的场地
        TennisCourtSelector selector = TennisCourtSelector.builder()
            .outDoorWeekdayHours(OUT_DOOR_WEEKDAY_HOURS)
            .outDoorWeekendHours(OUT_DOOR_WEEKEND_HOURS)
            .inDoorWeekdayHours(IN_DOOR_WEEKDAY_HOURS)
            .intDoorWeekendHours(IN_DOOR_WEEKEND_HOURS)
            .build();
        List<TennisCourt> available = selector.getAvailable(all);

        if (available.isEmpty()) {
            return;
        }

        // 获取唯一标识 
        String uniqueStr = TennisCourt.getUniqueString(available);
        String uniqueId = DigestUtils.md5Hex(uniqueStr);
        PictureStream pictureStream = pictureStreamCache.getPictureStream(TEENIS_STREAM);

        if (pictureStream.isSame(uniqueId)) {
            return;
        }

        List<TimeCell> timeTables = available.stream()
                .map(item -> new TimeCell(item.getDate(), item.getTimetableEnum()))
                .collect(Collectors.toList());

        // byte[] bytes = new DayTable().getByte(timeTables);
        byte[] a = new TimeTable().getByte(timeTables);

        pictureStream.update(uniqueId, Arrays.asList(a));

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
