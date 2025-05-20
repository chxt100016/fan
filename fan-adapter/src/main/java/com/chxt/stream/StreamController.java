package com.chxt.stream;

import com.chxt.stream.service.RtspStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/stream")
public class StreamController {
    private final RtspStreamService streamService;

    @Autowired
    public StreamController(RtspStreamService streamService) {
        this.streamService = streamService;
    }

    @PostMapping("/capture")
    public String captureStream(@RequestParam String rtspUrl) {
        try {
            streamService.captureStream(rtspUrl);
            return "Stream captured successfully";
        } catch (Exception e) {
            log.error("Failed to capture stream", e);
            return "Failed to capture stream: " + e.getMessage();
        }
    }

    @GetMapping("/status")
    public String getStatus() {
        return streamService.isRecording() ? "Recording" : "Idle";
    }
}
