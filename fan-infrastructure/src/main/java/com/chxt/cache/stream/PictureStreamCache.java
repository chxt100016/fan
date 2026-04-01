package com.chxt.cache.stream;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.chxt.domain.stream.PictureStream;

@Component
public class PictureStreamCache {

    private final Map<String, PictureStream> pictureStreamMap = new HashMap<>();

    public PictureStream getPictureStream(String name) {
        // 首先进行非同步检查
        PictureStream pictureStream = pictureStreamMap.get(name);
        if (pictureStream != null) {
            return pictureStream;
        }

        // 如果不存在，则进行同步
        synchronized (pictureStreamMap) {
            // 双重检查
            pictureStream = pictureStreamMap.get(name);
            if (pictureStream == null) {
                // 创建新的实例
                pictureStream = new PictureStream(name);
                pictureStreamMap.put(name, pictureStream);
            }
            return pictureStream;
        }
    }
    
}
