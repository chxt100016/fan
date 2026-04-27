package com.chxt.domain.notice;

import java.util.List;

public interface NoticeListener {

    void doNotice(byte[] cover, List<byte[]> pictureList);

}
