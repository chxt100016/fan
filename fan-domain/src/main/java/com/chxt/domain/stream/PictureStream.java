package com.chxt.domain.stream;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;

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
    
    private final String name;

    private boolean hasChange = false;

    private volatile List<byte[]> pictureList;

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

    public synchronized void update(List<byte[]> pictureList) {
        if (CollectionUtils.isEmpty(pictureList)) {
            return;
        }
        this.pictureList = pictureList;
        this.hasChange = true;
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
