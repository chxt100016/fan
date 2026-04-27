package com.chxt.domain.notice.impl;

import com.chxt.domain.notice.NoticeListener;
import com.chxt.domain.stream.PictureStream;

import java.util.List;

public class HomekitNotice implements NoticeListener {

    private final PictureStream pictureStream;

    public HomekitNotice(PictureStream pictureStream) {
        this.pictureStream = pictureStream;
    }


    @Override
    public void doNotice(byte[] cover, List<byte[]> pictureList) {
        this.pictureStream.update(cover, pictureList);
    }
}
