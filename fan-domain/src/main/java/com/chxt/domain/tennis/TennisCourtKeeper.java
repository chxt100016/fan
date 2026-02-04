package com.chxt.domain.tennis;

import com.chxt.domain.notice.NoticeListener;
import com.chxt.domain.pic.ScheduleImage;
import com.chxt.domain.pic.TimeCell;
import com.chxt.domain.pic.TimetableEnum;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class TennisCourtKeeper {

    private static final List<String> likelist = List.of(
            // 周一
            TimeCell.index().key(TimetableEnum.HL_OUT).monday().hourOfDay(8).getUniqueNo(),
            // 周二
            TimeCell.index().key(TimetableEnum.HL_OUT).tuesday().hourOfDay(8).getUniqueNo(),
            // 周三
            TimeCell.index().key(TimetableEnum.HL_OUT).wednesday().hourOfDay(8).getUniqueNo(),
            // 周四
            TimeCell.index().key(TimetableEnum.HL_OUT).thursday().hourOfDay(8).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).thursday().hourOfDay(9).getUniqueNo(),
            // 周五
            TimeCell.index().key(TimetableEnum.HL_OUT).friday().hourOfDay(8).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).friday().hourOfDay(19).getUniqueNo(),
            // 周六
            TimeCell.index().key(TimetableEnum.HL_OUT).saturday().hourOfDay(11).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).saturday().hourOfDay(12).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).saturday().hourOfDay(13).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).saturday().hourOfDay(14).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).saturday().hourOfDay(15).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).saturday().hourOfDay(16).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).saturday().hourOfDay(17).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(11).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(12).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(13).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(14).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(15).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(16).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(17).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(18).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).saturday().hourOfDay(19).getUniqueNo(),
            // 周日
            TimeCell.index().key(TimetableEnum.HL_OUT).sunday().hourOfDay(11).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).sunday().hourOfDay(12).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).sunday().hourOfDay(13).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).sunday().hourOfDay(14).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).sunday().hourOfDay(15).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).sunday().hourOfDay(16).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_OUT).sunday().hourOfDay(17).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(11).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(12).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(13).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(14).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(15).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(16).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(17).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(18).getUniqueNo(),
            TimeCell.index().key(TimetableEnum.HL_IN).sunday().hourOfDay(19).getUniqueNo()
    );

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
        java.nio.file.Files.write(java.nio.file.Paths.get("/Users/mac25/Downloads/123.png"), cover);
        listeners.forEach(item -> item.doNotice(cover, List.of(cover)));
    }

}
