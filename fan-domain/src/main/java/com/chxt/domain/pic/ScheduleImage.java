package com.chxt.domain.pic;

import javax.imageio.ImageIO;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Slf4j
public class ScheduleImage {

    private Map<String, List<String>> scheduleMap;

    public ScheduleImage(Map<String, List<String>> scheduleMap) {
        this.scheduleMap = scheduleMap;
    }

    @SneakyThrows
    public byte[] generate(){
        int width = 500;
        int groupHeight = 140;
        int height = scheduleMap.size() * groupHeight;
        int leftWidth = 100;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 抗锯齿处理
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 背景透明，清空画布
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);

        // 时间字体
        Font timeFont = new Font("微软雅黑", Font.PLAIN, 28);

        int groupIndex = 0;

        for (Map.Entry<String, List<String>> entry : scheduleMap.entrySet()) {
            String day = entry.getKey();
            List<String> times = entry.getValue();
            int yOffset = groupIndex * groupHeight;

            // 分割线（非第一组）
            if (groupIndex > 0) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(20, yOffset - 1, width - 40, 2);
            }

            // ========== 使用 typeA 绘制艺术字星期几 ==========
            // 计算居中坐标
            Font tempFont = new Font("微软雅黑", Font.BOLD, 60);
            FontRenderContext frc = g2d.getFontRenderContext();
            GlyphVector gv = tempFont.createGlyphVector(frc, day);
            Rectangle2D bounds = gv.getVisualBounds();
            double x = (leftWidth - bounds.getWidth()) / 2 - bounds.getX();
            double y = yOffset + (groupHeight - bounds.getHeight()) / 2 - bounds.getY();

            com.chxt.domain.utils.WriteFontUtils.typeA(g2d, x, y, day, 60);

            // ========== 绘制时间列表（自动换列） ==========
            g2d.setFont(timeFont);
            g2d.setColor(Color.WHITE);
            FontMetrics fmTime = g2d.getFontMetrics();

            int timeX = leftWidth + 20;
            int timeY = yOffset + 40;
            int lineHeight = fmTime.getHeight() + 6;
            int maxRows = 3; // 每列最多显示3个时间
            int colWidth = 120; // 每列宽度，增大间距

            for (int i = 0; i < times.size(); i++) {
                int col = i / maxRows;
                int row = i % maxRows;
                int drawX = timeX + col * colWidth;
                int drawY = timeY + row * lineHeight;
                g2d.drawString(times.get(i), drawX, drawY);
            }

            groupIndex++;
        }

        g2d.dispose();

        // 写入字节数组
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
