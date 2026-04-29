package com.chxt.domain.tennis;

import com.chxt.domain.pic.ScheduleImage;
import com.chxt.domain.pic.TimetableEnum;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class TennisCourtKeeper {

    private static final List<String> likelist = TennisCourt.buildUniqueNo()
            // 一
            .monday().key(TimetableEnum.HL_OUT).hour(8)
            // 二
            .tuesday().key(TimetableEnum.HL_OUT).hour(8)
            // 三
            .wednesday().key(TimetableEnum.HL_OUT).hour(8)
            // 四
            .thursday().key(TimetableEnum.HL_OUT).hour(8)
            // 五
            .friday().key(TimetableEnum.HL_OUT).hour(8, 19)
            .friday().key(TimetableEnum.HL_IN).hour(19,20)
            // 六
            .saturday().key(TimetableEnum.HL_OUT).hour(17, 18, 19, 20)
            // 日
            .sunday().key(TimetableEnum.HL_OUT).hour(17, 18, 19, 20)
            .getUniqueNo();

    private Map<String, TennisCourt> historyMap = new HashMap<>();

    private List<TennisCourt> likeItem = new ArrayList<>();

    public byte[] add(List<TennisCourt> tennisCourts) {
        if (CollectionUtils.isEmpty(tennisCourts)) {
            return null;
        }
        List<TennisCourt> likeIt = tennisCourts.stream()
                .filter(item -> likelist.contains(item.getUniqueNo()))
                .toList();
        if (CollectionUtils.isEmpty(likeIt)) {
            return null;
        }
        boolean shouldNotice = likeIt.stream().anyMatch(item -> !historyMap.containsKey(item.getUniqueNo()));

        historyMap.clear();
        tennisCourts.forEach(item -> historyMap.put(item.getUniqueNo(), item));

        if (!shouldNotice) {
            return null;
        }
        this.likeItem = likeIt;
        return this.getPic(likeIt);
    }

    @SneakyThrows
    private byte[] getPic(List<TennisCourt> list) {
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        Map<String,List<String>> dayOfWeekAndTimeMap = TennisCourt.getDayOfWeekAndTime(list);
        return new ScheduleImage(dayOfWeekAndTimeMap).generate();
    }

}
