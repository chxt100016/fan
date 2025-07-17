package com.chxt.domain.pic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.time.LocalDate;
import java.time.DayOfWeek;

import javax.imageio.ImageIO;


import com.chxt.domain.utils.DateStandardUtils;

import lombok.*;

public class TimeTable {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeTableConfig {
        // 时间点列表
        @Builder.Default
        private String[] times = {
            "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00", "19:00", "20:00", "21:00", "22:00",
        };

        // 定义时间列的宽度
        @Builder.Default
        private int timeColumnWidth = 80;

        // 图片大小
        @Builder.Default
        private int iconWidth = 35;
        @Builder.Default
        private int iconHeight = 35;

        // 定义表格右边框距离图像边际的距离
        @Builder.Default
        private int rightMargin = 20;

        // 定义交替背景颜色
        @Builder.Default
        private Color backgroundColor1 = Color.decode("#FFFFFF");
        @Builder.Default
        private Color backgroundColor2 = Color.decode("#FFFFFF");

        // 图片宽高
        @Builder.Default
        private int width = 1000;
        @Builder.Default
        private int height = 1000;

        // 今日标识
        @Builder.Default
        private String todayMarkerBase64 = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAAAXNSR0IArs4c6QAAAgVJREFUeF7tWtFtgzAQxUHskU7SRoDEFm1GySRhDCSMSidJBwHcngQ/BHqWjQuGl8/YmLvnd+8O+0Rw8J84uP+BNQBpmn6MQQzDsC6K4ntJcLMsO7dt+zZesyzL3OY91gAkSaLGBgghrraGjdckoJVS9/H/UkorH6weJmMAABiAEDiGBpAKN03zHgQBKfGTGtso8RLPKqWGrFNHUXTTzUJaIpgkCTn8uYSh/7EGgUGZSEpZc+9jAeh3/sEttLVxAiGKogvHBBaAOI7vQoinYmdrDk/Zo5TKq6q6/mWrDgAPIcTZB4fHNhILqqp6sQJgqtDxBYzDA0AbxZXKbAj4zAAAAAYgBKABEEHmwMQoC1CFdTqdvrZUD3Rd9zpVsTphgIsjL1swTY/MjBgAABwceoIBlgisHgJz5/acX0sdn68OwJwBHACcSnPPD+MAwPDiZLEsAAbM7ABHYYSA5d0eNKBHACIIETS7PkcW4FRa9/4faRBpcDoGOYahDthLHcDttOvx1esA1w5y6wOAtQshbodcj4MBYMDKpbBrinPrIwQQAggBo25yo8/ho12OUofo5lpjOVHsx2sp5cW2Tc5bABZplPS5VZZrkiRmsBpAkw7dLD3Ez9bb5cnOvks8/z1kuWlqhB4DdBfzcZ5WCPjomK7NAEAXqb3OAwP2urO6foEBukjtdd4PWjOVX5aRM1gAAAAASUVORK5CYII=";

        // 今日标识与星期文字之间的间距（像素）
        @Builder.Default
        private int todayMarkerSpacing = 4;
        
        // 字体设置
        @Builder.Default
        private int headerFontSize = 12;
        @Builder.Default
        private int timeFontSize = 12;
        @Builder.Default
        private String fontName = "Default";
        
        // 内部线条颜色
        @Builder.Default
        private Color innerLineColor = Color.decode("#CFD3DC");
        
        // 外边框颜色
        @Builder.Default
        private Color borderColor = Color.decode("#363637");
        
        // 今日标识大小
        @Builder.Default
        private int todayMarkerSize = 20;
    }
    
    
    
    private final TimeTableConfig config;
    
    public TimeTable() {
        this.config = TimeTableConfig.builder().build();
    }
    
    public TimeTable(TimeTableConfig config) {
        this.config = config;
    }
       
    
    public byte[] getByte(List<TimeCell> TimeCells) {
        // 直接创建RGB格式的图片
        BufferedImage bufferedImage = new BufferedImage(config.getWidth(), config.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // 绘制背景
        drawBackground(g2d);
        
        // 计算网格尺寸
        int rows = config.getTimes().length + 1; // 加一行用于显示星期几
        int cols = DateStandardUtils.DAY_EN.length;
        int cellWidth = (config.getWidth() - config.getTimeColumnWidth() - config.getRightMargin()) / cols;
        int cellHeight = config.getHeight() / rows;
        
        // 绘制网格
        drawGrid(g2d, rows, cols, cellWidth, cellHeight);
        
        // 绘制星期标题
        drawWeekdayHeaders(g2d, cellWidth, cellHeight);
        
        // 绘制时间列
        drawTimeColumns(g2d, cellHeight);
        
        // 绘制图标
        drawIcons(g2d, TimeCells, cellWidth, cellHeight);

        // 添加时间戳
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(config.getFontName(), Font.PLAIN, 12));
        FontMetrics metrics = g2d.getFontMetrics();
        int timeWidth = metrics.stringWidth(timestamp);
        g2d.drawString(timestamp, 
            config.getWidth() - timeWidth - 10,
            config.getHeight() - 10);

        g2d.dispose();

        // 将图像转换为字节数组
        return convertToByteArray(bufferedImage);
    }
    
    private void drawBackground(Graphics2D g2d) {
        // 设置整体背景颜色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, config.getWidth(), config.getHeight());
    }
    
    private void drawGrid(Graphics2D g2d, int rows, int cols, int cellWidth, int cellHeight) {
        // 绘制背景颜色交替的行
        for (int i = 1; i < config.getTimes().length; i++) {
            if (i % 2 == 0) {
                g2d.setColor(config.getBackgroundColor1());
            } else {
                g2d.setColor(config.getBackgroundColor2());
            }
            g2d.fillRect(config.getTimeColumnWidth(), i * cellHeight, 
                    config.getWidth() - config.getTimeColumnWidth() - config.getRightMargin(), 
                    cellHeight);
        }

        // 设置内部线条颜色
        g2d.setColor(config.getInnerLineColor());

        // 绘制内部横线
        for (int i = 1; i < config.getTimes().length; i++) {
            g2d.drawLine(config.getTimeColumnWidth(), i * cellHeight, 
                    config.getWidth() - config.getRightMargin(), i * cellHeight);
        }

        // 绘制内部竖线
        for (int i = 0; i < cols; i++) {
            g2d.drawLine(config.getTimeColumnWidth() + i * cellWidth, cellHeight, 
                    config.getTimeColumnWidth() + i * cellWidth,
                    config.getTimes().length * cellHeight);
        }

        // 设置外边框颜色
        g2d.setColor(config.getBorderColor());

        // 绘制外边框
        g2d.drawRect(config.getTimeColumnWidth(), cellHeight, 
                config.getWidth() - config.getTimeColumnWidth() - config.getRightMargin(),
                config.getTimes().length * cellHeight - cellHeight);
    }
    
    private void drawWeekdayHeaders(Graphics2D g2d, int cellWidth, int cellHeight) {
        // 设置星期字体为粗体并居中显示
        g2d.setFont(new Font(config.getFontName(), Font.BOLD, config.getHeaderFontSize()));
        g2d.setColor(Color.BLACK);
        FontMetrics metrics = g2d.getFontMetrics();
        
        for (int i = 0; i < DateStandardUtils.DAY_EN.length; i++) {
            String dayText = DateStandardUtils.DAY_EN[i];
            int textWidth = metrics.stringWidth(dayText);
            int x = config.getTimeColumnWidth() + i * cellWidth + (cellWidth - textWidth) / 2;
            int y = (cellHeight + metrics.getAscent()) / 2; // 垂直居中
            g2d.drawString(dayText, x, y);
        }

        // 绘制今日标识
        drawTodayMarker(g2d, cellWidth, cellHeight, metrics);
    }
    
    private void drawTodayMarker(Graphics2D g2d, int cellWidth, int cellHeight, FontMetrics metrics) {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int todayIndex = dayOfWeek.getValue() - 1; // 转换为0-6的索引

        try {
            byte[] markerImageBytes = Base64.getDecoder().decode(config.getTodayMarkerBase64());
            BufferedImage markerImage = ImageIO.read(new ByteArrayInputStream(markerImageBytes));
            int markerX = config.getTimeColumnWidth() + todayIndex * cellWidth + (cellWidth - config.getTodayMarkerSize()) / 2;
            int markerY = cellHeight / 2 + metrics.getAscent() / 2 + config.getTodayMarkerSpacing();
            g2d.drawImage(markerImage, markerX, markerY, config.getTodayMarkerSize(), config.getTodayMarkerSize(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void drawTimeColumns(Graphics2D g2d, int cellHeight) {
        // 填充时间点
        g2d.setFont(new Font(config.getFontName(), Font.PLAIN, config.getTimeFontSize()));
        for (int i = 0; i < config.getTimes().length; i++) {
            g2d.drawString(config.getTimes()[i], config.getTimeColumnWidth() / 4, (i + 1) * cellHeight);
        }
    }
    
    private void drawIcons(Graphics2D g2d, List<TimeCell> TimeCells, int cellWidth, int cellHeight) {
        // 渲染图片
        Map<String, List<String>> renderedKeys = new HashMap<>();
        for (TimeCell TimeCell : TimeCells) {
            Integer dayIndex = DateStandardUtils.getDayOfWeek(TimeCell.getDate());
            int timeIndex = Arrays.asList(config.getTimes()).indexOf(TimeCell.getTime());


            
            if (dayIndex >= 0 && dayIndex < DateStandardUtils.DAY_EN.length && timeIndex != -1) {
                String cellKey = dayIndex + "-" + timeIndex;
                renderedKeys.putIfAbsent(cellKey, new ArrayList<>());

                if (!renderedKeys.get(cellKey).contains(TimeCell.getKey())) {
                    renderedKeys.get(cellKey).add(TimeCell.getKey());
                }
            }
        }

        for (String cellKey : renderedKeys.keySet()) {
            String[] indices = cellKey.split("-");
            int dayIndex = Integer.parseInt(indices[0]);
            int timeIndex = Integer.parseInt(indices[1]);
            List<String> keys = renderedKeys.get(cellKey);

            int centerX = config.getTimeColumnWidth() + dayIndex * cellWidth + cellWidth / 2;
            int imageY = (timeIndex + 1) * cellHeight + (cellHeight - config.getIconHeight()) / 2;

            int totalWidth = keys.size() * config.getIconWidth();
            int startX = centerX - totalWidth / 2;

            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                TimeCell TimeCell = TimeCells.stream()
                        .filter(data -> data.getKey().equals(key))
                        .findFirst()
                        .orElse(null);
                if (TimeCell != null) {
                    drawIcon(g2d, TimeCell, startX + i * config.getIconWidth(), imageY);
                }
            }
        }
    }
    
    private void drawIcon(Graphics2D g2d, TimeCell cell, int x, int y) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(cell.getIconBase64());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            g2d.drawImage(image, x, y, config.getIconWidth(), config.getIconHeight(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private byte[] convertToByteArray(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    
}
