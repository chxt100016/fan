package com.chxt.web.video;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequestMapping("/video")
@RestController
public class EtcVideo {
    private static final Logger log = LoggerFactory.getLogger(EtcVideo.class);
    private static final long CHUNK_SIZE = 1024 * 1024; // 1MB 块大小
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024 * 1024; // 最大支持10GB文件
    private static final long EXPIRE_TIME = 604800000L; // 7天，单位毫秒

    @GetMapping("/etc")
    public ResponseEntity<byte[]> etc(@RequestParam(name = "path", required = true) String path,
                                    @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            long fileSize = Files.size(filePath);
            
            if (fileSize > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept-Ranges", "bytes");
            headers.add("Content-Type", "video/mp4");
            headers.add("Cache-Control", "max-age=604800");
            headers.add("Expires", String.valueOf(System.currentTimeMillis() + EXPIRE_TIME));

            // 处理Range请求
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                try {
                    String[] ranges = rangeHeader.substring(6).split("-");
                    long start = 0;
                    long end = fileSize - 1;

                    if (ranges.length > 0 && !ranges[0].isEmpty()) {
                        start = Long.parseLong(ranges[0]);
                    }
                    
                    if (ranges.length > 1 && !ranges[1].isEmpty()) {
                        end = Math.min(Long.parseLong(ranges[1]), fileSize - 1);
                    } else {
                        end = Math.min(start + CHUNK_SIZE - 1, fileSize - 1);
                    }

                    // 验证范围
                    if (start < 0 || end < 0 || start >= fileSize) {
                        headers.add("Content-Range", "bytes */" + fileSize);
                        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                                .headers(headers)
                                .build();
                    }

                    // 确保end不超过文件大小
                    end = Math.min(end, fileSize - 1);
                    // 确保start不超过end
                    start = Math.min(start, end);

                    long contentLength = end - start + 1;
                    headers.add("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
                    headers.add("Content-Length", String.valueOf(contentLength));

                    // 使用缓冲流读取文件片段
                    byte[] data = new byte[(int) contentLength];
                    try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
                        file.seek(start);
                        int bytesRead = file.read(data, 0, (int) contentLength);
                        if (bytesRead < contentLength) {
                            // 如果读取的字节数小于预期，调整数组大小
                            byte[] actualData = new byte[bytesRead];
                            System.arraycopy(data, 0, actualData, 0, bytesRead);
                            data = actualData;
                            headers.set("Content-Length", String.valueOf(bytesRead));
                            headers.set("Content-Range", String.format("bytes %d-%d/%d", start, start + bytesRead - 1, fileSize));
                        }
                    }

                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .headers(headers)
                            .body(data);
                } catch (NumberFormatException e) {
                    // 如果Range头部格式错误，返回整个文件的第一个块
                    rangeHeader = null;
                }
            }

            // 对于非Range请求或Range解析失败，返回第一个块
            long contentLength = Math.min(CHUNK_SIZE, fileSize);
            headers.add("Content-Length", String.valueOf(contentLength));
            byte[] data = new byte[(int) contentLength];
            
            try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
                int bytesRead = file.read(data, 0, (int) contentLength);
                if (bytesRead < contentLength) {
                    // 如果读取的字节数小于预期，调整数组大小
                    byte[] actualData = new byte[bytesRead];
                    System.arraycopy(data, 0, actualData, 0, bytesRead);
                    data = actualData;
                    headers.set("Content-Length", String.valueOf(bytesRead));
                }
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);

        } catch (IOException e) {
            log.error("Error processing video file: " + path, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
