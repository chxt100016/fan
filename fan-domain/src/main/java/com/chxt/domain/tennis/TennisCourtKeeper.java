package com.chxt.domain.tennis;

import com.chxt.domain.notice.NoticeListener;
import com.chxt.domain.pic.ScheduleImage;
import com.chxt.domain.pic.TimetableEnum;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

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
            // 六
            .saturday().key(TimetableEnum.HL_OUT, TimetableEnum.HL_IN).hour(10, 11, 12, 13, 14, 15, 16, 17)
            // 日
            .sunday().key(TimetableEnum.HL_OUT, TimetableEnum.HL_IN).hour(10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
            .getUniqueNo();

    private Map<String, TennisCourt> historyMap = new HashMap<>();

    private List<NoticeListener> listeners;

    public TennisCourtKeeper (NoticeListener listener) {
        this.listeners = List.of(listener);
    }

    public void add(List<TennisCourt> tennisCourts) {
        if (CollectionUtils.isEmpty(tennisCourts)) {
            return;
        }
        List<TennisCourt> likeIt = tennisCourts.stream()
                .filter(item -> likelist.contains(item.getUniqueNo()))
                .toList();
        if (CollectionUtils.isEmpty(likeIt)) {
            return;
        }
        boolean shouldNotice = likeIt.stream().anyMatch(item -> historyMap.containsKey(item.getUniqueNo()));

        historyMap.clear();
        tennisCourts.forEach(item -> historyMap.put(item.getUniqueNo(), item));

        if (shouldNotice) {
            this.doNotice(likeIt);
        }
    }

    @SneakyThrows
    private void doNotice(List<TennisCourt> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
//        byte[] a = new TimeTable().getByte(list.stream().map(TennisDTO::getTimeCell).toList());

        Map<String,List<String>> dayOfWeekAndTimeMap = TennisCourt.getDayOfWeekAndTime(list);
        byte[] cover = new ScheduleImage(dayOfWeekAndTimeMap).generate();
        listeners.forEach(item -> item.doNotice(cover, List.of(cover)));
    }

}
