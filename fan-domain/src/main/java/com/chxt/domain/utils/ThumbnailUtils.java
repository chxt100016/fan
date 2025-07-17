package com.chxt.domain.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


import net.coobird.thumbnailator.Thumbnails;

public class ThumbnailUtils {
    
    // 默认缩略图缩放比例
    private static final double DEFAULT_SCALE_RATIO = 0.5;

    /**
     * 生成单图缩略图（使用默认压缩比例0.5）
     * @param imageData 原图数据
     * @return 压缩后的图片数据
     * @throws IOException IO异常
     */
    public static byte[] generate(byte[] imageData) throws IOException {
        return generate(imageData, DEFAULT_SCALE_RATIO);
    }

    /**
     * 生成单图缩略图
     * @param imageData 原图数据
     * @param scaleRatio 压缩比例
     * @return 压缩后的图片数据
     * @throws IOException IO异常
     */
    public static byte[] generate(byte[] imageData, double scaleRatio) throws IOException {
        if (imageData == null || imageData.length == 0) {
            throw new IllegalArgumentException("图片数据不能为空");
        }
        if (scaleRatio <= 0 || scaleRatio > 1) {
            throw new IllegalArgumentException("压缩比例必须在0-1之间");
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .scale(scaleRatio)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 生成四宫格缩略图（使用默认压缩比例0.5）
     * @param images 四张图片的数据列表
     * @return 压缩后的图片数据
     * @throws IOException IO异常
     */
    public static byte[] generate(List<byte[]> images) throws IOException {
        return generate(images, DEFAULT_SCALE_RATIO);
    }

    /**
     * 生成四宫格缩略图
     * @param images 四张图片的数据列表
     * @param scaleRatio 压缩比例
     * @return 压缩后的图片数据
     * @throws IOException IO异常
     */
    public static byte[] generate(List<byte[]> images, double scaleRatio) throws IOException {
        // 参数校验
        if (images == null || images.size() != 4) {
            throw new IllegalArgumentException("必须提供4张图片");
        }
        if (scaleRatio <= 0 || scaleRatio > 1) {
            throw new IllegalArgumentException("压缩比例必须在0-1之间");
        }

        // 先合并成四宫格
        byte[] mergedImage = mergeQuadImages(images);
        
        // 再进行压缩 （合成后变成了原有2倍， 先恢复原有尺寸再压缩）
        return generate(mergedImage, scaleRatio * 0.5);
    }

    /**
     * 合并四张图片成四宫格，直接拼接不缩放
     * @param images 四张图片的数据列表
     * @return 合并后的图片数据
     * @throws IOException IO异常
     */
    private static byte[] mergeQuadImages(List<byte[]> images) throws IOException {
        // 读取所有图片
        List<BufferedImage> bufferedImages = new ArrayList<>();
        for (byte[] imageData : images) {
            bufferedImages.add(ImageIO.read(new ByteArrayInputStream(imageData)));
        }

        // 获取单个图片的尺寸
        int singleWidth = bufferedImages.get(0).getWidth();
        int singleHeight = bufferedImages.get(0).getHeight();

        // 创建一个新的图片，尺寸是原图的两倍
        BufferedImage combined = new BufferedImage(singleWidth * 2, singleHeight * 2, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combined.createGraphics();

        // 直接绘制四张图片到对应位置
        for (int i = 0; i < 4; i++) {
            // 计算位置
            int x = (i % 2) * singleWidth;  // 0, singleWidth, 0, singleWidth
            int y = (i / 2) * singleHeight; // 0, 0, singleHeight, singleHeight
            
            // 直接绘制原图到对应位置
            g2d.drawImage(bufferedImages.get(i), x, y, null);
        }
        g2d.dispose();

        // 转换为byte数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(combined, "JPEG", outputStream);
        return outputStream.toByteArray();
    }

}
