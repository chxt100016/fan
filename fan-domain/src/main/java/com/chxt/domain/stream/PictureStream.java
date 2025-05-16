package com.chxt.domain.stream;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.SneakyThrows;

public class PictureStream {
    
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
    private final String name;

    /**
     * 是否变化
     */
    private volatile boolean hasChange = false;

    /**
     * 图片列表
     */
    private volatile List<byte[]> pictureList;

    /**
     * 唯一标识
     */
    private volatile String uniqueId;

    public PictureStream(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public synchronized boolean check() {
        if (this.hasChange) {
            this.hasChange = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isSame(String uniqueId) {
        return StringUtils.equals(this.uniqueId, uniqueId);
    }

    public synchronized void update(String uniqueId, List<byte[]> pictureList) {
        if (CollectionUtils.isEmpty(pictureList) || StringUtils.isBlank(uniqueId)) {
            return;
        }

        if (this.isSame(uniqueId)) {
            return;
        }

        this.pictureList = pictureList;
        this.hasChange = true;
        this.uniqueId = uniqueId;
    }

    public synchronized byte[] getStillImage() {
        if (CollectionUtils.isEmpty(this.pictureList)) {
            return new byte[0];
        }
        return this.pictureList.get(0);
    }

    @SneakyThrows
    public void stream(OutputStream outputStream, int interval) {
        int index = 0;
        while(true) {
            byte[] imageBytes = pictureList.get(index);
            outputStream.write(String.format(HEADER, imageBytes.length).getBytes());
            outputStream.write(imageBytes);
            outputStream.write("\r\n".getBytes());
            outputStream.flush();
            TimeUnit.MILLISECONDS.sleep(interval);
            index = (index + 1) % pictureList.size();
        }
    }

}
