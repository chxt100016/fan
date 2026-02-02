package com.chxt.domain.notice;

import com.chxt.domain.pic.TimeCell;

import java.util.List;

public interface NoticeListener {

    void doNotice(byte[] cover, List<byte[]> pictureList);

}
