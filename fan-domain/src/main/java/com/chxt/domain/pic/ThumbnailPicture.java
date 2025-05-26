package com.chxt.domain.pic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;


public class ThumbnailPicture {
    
    // 缩略图缩放比例
    public static double SCALE_RATIO = 0.5;
    
    private byte[] originalImage;
    private byte[][] quadImages;
    private boolean isQuad;

    // 单图构造函数
    public ThumbnailPicture(byte[] imageData) {
        this.originalImage = imageData;
        this.isQuad = false;
    }

    // 四宫格构造函数
    public ThumbnailPicture(byte[] topLeft, byte[] topRight, byte[] bottomLeft, byte[] bottomRight) {
        this.quadImages = new byte[][]{topLeft, topRight, bottomLeft, bottomRight};
        this.isQuad = true;
    }

    // 生成缩略图
    public byte[] generateThumbnail() throws IOException {
        if (isQuad) {
            return generateQuadThumbnail();
        } else {
            return generateSingleThumbnail();
        }
    }

    // 生成单图缩略图
    private byte[] generateSingleThumbnail() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(originalImage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .scale(SCALE_RATIO)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    // 生成四宫格缩略图
    private byte[] generateQuadThumbnail() throws IOException {
        // 读取第一张图片获取尺寸
        BufferedImage firstImage = ImageIO.read(new ByteArrayInputStream(quadImages[0]));
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();

        // 计算每个小图的尺寸（原尺寸的一半）
        int subWidth = width / 2;
        int subHeight = height / 2;

        // 创建一个新的图片，用于组合四张图片
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combined.createGraphics();

        // 为每个子图创建缩略图并绘制到对应位置
        for (int i = 0; i < 4; i++) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(quadImages[i]);
            BufferedImage subImage = Thumbnails.of(ImageIO.read(inputStream))
                    .size(subWidth, subHeight)
                    .asBufferedImage();
            
            // 计算位置
            int x = (i % 2) * subWidth;  // 0, subWidth, 0, subWidth
            int y = (i / 2) * subHeight; // 0, 0, subHeight, subHeight
            
            // 绘制到对应位置
            g2d.drawImage(subImage, x, y, null);
        }
        g2d.dispose();

        // 生成最终的缩略图
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(combined)
                .scale(SCALE_RATIO)
                .outputFormat("JPEG")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    // 测试主方法
    public static void main(String[] args) {
        try {
            // // 读取单张图片
            // byte[] imageData = Files.readAllBytes(Paths.get("D:\\1.jpg"));
            
            // // 创建单图缩略图
            // ThumbnailPicture singlePic = new ThumbnailPicture(imageData);
            // byte[] thumbnail = singlePic.generateThumbnail();
            // Files.write(Paths.get("D:\\output_single.jpg"), thumbnail);

            // 读取四张图片
            byte[] img1 = Files.readAllBytes(Paths.get("D:\\1.jpg"));
            byte[] img2 = Files.readAllBytes(Paths.get("D:\\2.jpg"));
            byte[] img3 = Files.readAllBytes(Paths.get("D:\\3.jpg"));
            byte[] img4 = Files.readAllBytes(Paths.get("D:\\4.jpg"));

            // 创建四宫格缩略图
            ThumbnailPicture quadPic = new ThumbnailPicture(img1, img2, img3, img4);
            byte[] quadThumbnail = quadPic.generateThumbnail();
            Files.write(Paths.get("D:\\output_quad.jpg"), quadThumbnail);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
