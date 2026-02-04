package com.chxt.domain.stream;

import com.chxt.domain.notice.NoticeListener;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PictureStream implements NoticeListener {
    
    /**
     * 标准MJPEG格式头， 一个空格都改不得
     */
    private final static String HEADER = 
            """
            --frame
            Content-Type: image/jpeg
            Content-Length: %d
            
            """;    
    
    /**
     * 名称
     */
    @Getter
    private final String name;

    /**
     * 是否变化
     */
    private volatile boolean hasChange = false;

    /**
     * 封面
     */
    private volatile byte[] cover;
    
    /**
     * 图片列表
     */
    private volatile List<byte[]> pictureList;

    public PictureStream(String name) {
        this.name = name;
    }

    public synchronized boolean check() {
        if (this.hasChange) {
            this.hasChange = false;
            return true;
        } else {
            return false;
        }
    }



    public synchronized void update(byte[] cover, List<byte[]> pictureList) {
        if (CollectionUtils.isEmpty(pictureList)) {
            return;
        }
        this.pictureList = pictureList;
        this.cover = cover;
        this.hasChange = true;
    }

    public void update(List<byte[]> pictureList) {
        if (CollectionUtils.isEmpty(pictureList)) {
            return;
        }
        this.update(pictureList.get(0), pictureList);
    }

    public synchronized byte[] getCover() {
        if (CollectionUtils.isEmpty(this.pictureList)) {
            log.error("{}: image cover is empty", this.name);
            return new byte[0];
        }
        return this.cover;
    }

    @SneakyThrows
    public void stream(OutputStream outputStream, int interval, int fps) {
        if (CollectionUtils.isEmpty(this.pictureList)) {
            log.error("{}: stream pictureList is empty", this.name);
            return;
        }

        int index = 0;
        int fpt = 1000 / fps;
        
        while(true) {
            byte[] imageBytes = pictureList.get((index / interval) % pictureList.size());
            outputStream.write(String.format(HEADER, imageBytes.length).getBytes());
            outputStream.write(imageBytes);
            outputStream.write("\r\n".getBytes());
            outputStream.flush();
            TimeUnit.MILLISECONDS.sleep(fpt);
            index += fpt;
        }
    }

    @Override
    public void doNotice(byte[] cover, List<byte[]> pictureList) {
        this.update(cover, pictureList);
    }
}
