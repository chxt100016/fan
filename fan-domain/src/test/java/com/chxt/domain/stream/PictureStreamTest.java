package com.chxt.domain.stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PictureStreamTest {

    private PictureStream pictureStream;

    @BeforeEach
    void setUp() {
        pictureStream = new PictureStream("test-stream");
    }

    @Test
    void 应该能够更新封面和图片列表() {
        byte[] cover = new byte[]{1, 2, 3};
        List<byte[]> pictureList = List.of(new byte[]{4, 5, 6});

        pictureStream.update(cover, pictureList);

        assertArrayEquals(cover, pictureStream.getCover());
    }

    @Test
    void 当图片列表为空时更新应该不生效() {
        byte[] cover = new byte[]{1, 2, 3};
        List<byte[]> emptyList = Collections.emptyList();

        pictureStream.update(cover, emptyList);

        assertArrayEquals(new byte[0], pictureStream.getCover());
    }

    @Test
    void 当图片列表为null时更新应该不生效() {
        byte[] cover = new byte[]{1, 2, 3};

        pictureStream.update(cover, null);

        assertArrayEquals(new byte[0], pictureStream.getCover());
    }

    @Test
    void 更新后check应该返回true() {
        byte[] cover = new byte[]{1, 2, 3};
        List<byte[]> pictureList = List.of(new byte[]{4, 5, 6});

        pictureStream.update(cover, pictureList);

        assertTrue(pictureStream.check());
    }

    @Test
    void 没有更新时check应该返回false() {
        assertFalse(pictureStream.check());
    }

    @Test
    void check调用后再次调用应该返回false() {
        byte[] cover = new byte[]{1, 2, 3};
        List<byte[]> pictureList = List.of(new byte[]{4, 5, 6});

        pictureStream.update(cover, pictureList);
        pictureStream.check();

        assertFalse(pictureStream.check());
    }

    @Test
    void 应该能够使用单参数update() {
        byte[] image = new byte[]{1, 2, 3};
        List<byte[]> pictureList = List.of(image);

        pictureStream.update(pictureList);

        assertArrayEquals(image, pictureStream.getCover());
    }

    @Test
    void 作为NoticeListener应该能够接收通知() {
        byte[] cover = new byte[]{1, 2, 3};
        List<byte[]> pictureList = List.of(new byte[]{4, 5, 6});

        pictureStream.doNotice(cover, pictureList);

        assertArrayEquals(cover, pictureStream.getCover());
        assertTrue(pictureStream.check());
    }

    @Test
    void stream方法应该输出MJPEG格式数据() throws IOException, InterruptedException {
        byte[] image1 = new byte[]{1, 2, 3};
        byte[] image2 = new byte[]{4, 5, 6};
        List<byte[]> pictureList = List.of(image1, image2);

        pictureStream.update(image1, pictureList);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pictureStream.stream(outputStream, 10, 10);

        byte[] output = outputStream.toByteArray();
        assertTrue(output.length > 0);
    }

    @Test
    void 当图片列表为空时stream应该记录错误() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        pictureStream.stream(outputStream, 10, 10);

        assertEquals(0, outputStream.size());
    }
}
