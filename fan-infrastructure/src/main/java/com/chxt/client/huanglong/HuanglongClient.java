package com.chxt.client.huanglong;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import com.chxt.client.huanglong.model.BookInfoRequest;
import com.chxt.client.huanglong.model.BookInfoResponse;
import com.chxt.domain.pic.TimetableEnum;
import com.chxt.domain.tennis.TennisCourt;
import com.chxt.domain.utils.Http;


@Component
public class HuanglongClient {

    private final static String uri = "https://field.hlsports.net/api/orderlists/get/book";


    //
    public List<TennisCourt> getOutdoorAndIndoor(int dayRange) {
        Date today = new Date();
        List<TennisCourt> res = new ArrayList<>();
        for (int i = 0; i < dayRange; i ++ ) {
            Date date = DateUtils.addDays(today, i);
            // 黄龙室外
            List<TennisCourt> outdoor = this.getOuterBookInfo(date);
            res.addAll(outdoor);

            // 黄龙室内
            List<TennisCourt> indoor = this.getInnerBookInfo(date);
            res.addAll(indoor);
        }
        return res;

    }

    public List<TennisCourt> getOuterBookInfo(Date date) {
        BookInfoRequest entity = BookInfoRequest.builder()
                .orderDateNum(date.getTime())
                ._venue("59cdec566f1d65ba08316176")
                ._item("59cc95ca42fa6b6703843bba")
                .passBaseOn("start")
                .showLine("row")
                .showPassTime(true)
                ._org("59cb5c718e1e92a702eca340")
                .build();
        BookInfoResponse response = Http
                .uri(uri)
                .jsonHeader()
                .entity(entity)
                .doPost()
                .result(BookInfoResponse.class);
        return response.toTennisCourt(date, TimetableEnum.HL_OUTDOOR);
    }

    public List<TennisCourt> getInnerBookInfo(Date date) {
        BookInfoRequest entity = BookInfoRequest.builder()
                .orderDateNum(date.getTime())
                ._venue("59cc969742fa6b6703843bbe")
                ._item("5fbb0b6f47c6f60ffbd98483")
                .passBaseOn("start")
                .showLine("row")
                .showPassTime(true)
                ._org("59cb5c718e1e92a702eca340")
                .build();
        BookInfoResponse response = Http
                .uri(uri)
                .jsonHeader()
                .entity(entity)
                .doPost()
                .result(BookInfoResponse.class);

        return response.toTennisCourt(date, TimetableEnum.HL_INDOOR);
    }

}
