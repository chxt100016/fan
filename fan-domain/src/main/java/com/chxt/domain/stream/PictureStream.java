package com.chxt.domain.stream;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureStream {
    
    private final static String HEADER = """
            --frame\r\n
            Content-Type: image/jpeg\r\n
            Content-Length: %d\r\n
            \r\n
            """;
  

    public List<byte[]> pictureList;

    @SneakyThrows
    public void write(OutputStream outputStream, int interval) {
        for (byte[] imageBytes : pictureList) {
            outputStream.write(String.format(HEADER, imageBytes.length).getBytes());
            outputStream.write(imageBytes);
            outputStream.write("\r\n".getBytes());
            outputStream.flush();
            TimeUnit.MILLISECONDS.sleep(interval);
        }
        outputStream.flush();
    }

}
