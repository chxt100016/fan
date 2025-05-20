package com.chxt.stream.service;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class RtspStreamService {
    private final List<byte[]> frameDataList = new ArrayList<>();
    private final AtomicBoolean isRecording = new AtomicBoolean(false);

    public void captureStream(String rtspUrl) {
        if (isRecording.get()) {
            log.warn("Already recording stream");
            return;
        }

        frameDataList.clear();
        isRecording.set(true);

        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
            grabber.start();

            long startTime = System.currentTimeMillis();
            Frame frame;

            while (isRecording.get() && (System.currentTimeMillis() - startTime) < 10000) {
                frame = grabber.grab();
                if (frame == null) {
                    continue;
                }

                if (frame.image != null || frame.samples != null) {
                    ByteBuffer frameBuffer = frame.data;
                    byte[] frameData = new byte[frameBuffer.remaining()];
                    frameBuffer.get(frameData);
                    frameDataList.add(frameData);
                }
            }

            grabber.stop();
            grabber.release();
        } catch (Exception e) {
            log.error("Error capturing RTSP stream", e);
        } finally {
            isRecording.set(false);
        }
    }

    public List<byte[]> getFrameData() {
        return frameDataList;
    }

    public boolean isRecording() {
        return isRecording.get();
    }
} 