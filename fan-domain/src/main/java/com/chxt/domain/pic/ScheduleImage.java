package com.chxt.domain.pic;

import javax.imageio.ImageIO;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@Slf4j
public class ScheduleImage {

    // 默认配置项
    private static final Font TIME_FONT = new Font("阿里巴巴普惠体", Font.PLAIN, 15);
    private static final Font DAY_FONT = new Font("阿里巴巴普惠体", Font.BOLD, 60);
    private static final int DAY_WIDTH = 100; // 星期区域宽度
    private static final int TIME_COL_WIDTH = 50; // 时间列宽度
    private static final int imageWidth = 800; // 图片宽度
    private static final int cardWidth = 500; // 卡片宽度
    private static final int groupHeight = 100; // 每组高度
    private static final int topMargin = 20; // 顶部边距

    private final Map<String, List<String>> scheduleMap;
    
    
    public ScheduleImage(Map<String, List<String>> scheduleMap) {
        this.scheduleMap = scheduleMap;
    }

    @SneakyThrows
    public byte[] generate() {
        int height = topMargin + scheduleMap.size() * groupHeight;
        BufferedImage image = new BufferedImage(imageWidth, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 清空背景
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, imageWidth, height);
        g2d.setComposite(AlphaComposite.SrcOver);

        int groupIndex = 0;
        int cardStartX = (imageWidth - cardWidth) / 2;

        for (Map.Entry<String, List<String>> entry : scheduleMap.entrySet()) {
            int cardY = topMargin + groupIndex * groupHeight;

            // 分割线（非第一个）
            if (groupIndex > 0) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(cardStartX, cardY - 1, cardWidth, 2);
            }

            drawCard(g2d, entry.getKey(), entry.getValue(), cardStartX, cardY);
            groupIndex++;
        }

        g2d.dispose();

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private void drawCard(Graphics2D g2d, String day, List<String> times, int cardStartX, int cardStartY) {
        // ==== 绘制星期 ====
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = DAY_FONT.createGlyphVector(frc, day);
        Rectangle2D bounds = gv.getVisualBounds();
        double dayX = cardStartX + (DAY_WIDTH - bounds.getWidth()) / 2 - bounds.getX();
        double dayY = cardStartY + (groupHeight - bounds.getHeight()) / 2 - bounds.getY();

        com.chxt.domain.utils.WriteFontUtils.typeA(g2d, dayX, dayY, day, 60);

        // ==== 绘制时间 ====
        g2d.setFont(TIME_FONT);
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight() + 6;
        int maxRows = Math.max((groupHeight - 20) / lineHeight, 1);

        int timeX = cardStartX + DAY_WIDTH;
        int timeY = cardStartY + 30;

        int timeAreaWidth = cardWidth - DAY_WIDTH;
        int maxCols = Math.max(1, timeAreaWidth / TIME_COL_WIDTH);
        int displayedCount = Math.min(times.size(), maxRows * maxCols);

        for (int i = 0; i < displayedCount; i++) {
            int col = i / maxRows;
            int row = i % maxRows;
            int drawX = timeX + col * TIME_COL_WIDTH;
            int drawY = timeY + row * lineHeight;
            g2d.drawString(times.get(i), drawX, drawY);
        }

        // 多余的时间数量，显示为 +N
        if (displayedCount < times.size()) {
            int remaining = times.size() - displayedCount;
            String extra = "+" + remaining;
            int col = displayedCount / maxRows;
            int row = displayedCount % maxRows;
            int drawX = timeX + col * TIME_COL_WIDTH;
            int drawY = timeY + row * lineHeight;
            g2d.drawString(extra, drawX, drawY);
        }
    }

    public static void main(String[] args) {
        Map<String, List<String>> schedule = new HashMap<>();
        schedule.put("一", Arrays.asList("08:00", "10:00", "14:00", "16:00", "18:00"));
        schedule.put("二", Arrays.asList("09:00-11:00", "11:00-13:00"));
        schedule.put("三", Arrays.asList("08:30-10:30", "10:30-12:30", "14:30-16:30"));
        schedule.put("四", Arrays.asList("09:30-11:30"));

        ScheduleImage imageGenerator = new ScheduleImage(schedule);
        try {
            byte[] imageBytes = imageGenerator.generate();
            java.nio.file.Files.write(java.nio.file.Paths.get("schedule.png"), imageBytes);
            System.out.println("Schedule image generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
