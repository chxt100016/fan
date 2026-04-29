package com.chxt.domain.notice.gateway;


import com.chxt.domain.stream.PictureStream;
import org.springframework.stereotype.Component;

@Component
public interface PictureStreamCacheGateway {

    PictureStream getPictureStream(String name);
}
