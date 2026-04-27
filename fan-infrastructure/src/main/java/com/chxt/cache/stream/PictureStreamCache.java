package com.chxt.cache.stream;

import com.chxt.domain.stream.PictureStream;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
            pictureStream = pictureStreamMap.computeIfAbsent(name, PictureStream::new);
            // 创建新的实例
            return pictureStream;
        }
    }
    
}
