package com.chxt.domain.pic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import java.util.Base64;
import java.io.ByteArrayInputStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DayTable {
    // 配置属性，全部有默认值
    @Builder.Default
    private int marginLeft = 30;

    @Builder.Default
    private int marginTop = 30;

    @Builder.Default
    private int marginRight = 30;

    @Builder.Default
    private int marginBottom = 30;

    @Builder.Default
    private Color outerBorderColor = Color.BLACK;

    @Builder.Default
    private Color innerBorderColor = new Color(220, 220, 220);

    @Builder.Default
    private Color headerTextColor = new Color(0, 0, 0);

    @Builder.Default
    private Color contentTextColor = new Color(30, 144, 255, 220); 

    @Builder.Default
    private Color timeTextColor = new Color(100, 100, 100);
    
    @Builder.Default
    private String todayIconBase64 = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAAAXNSR0IArs4c6QAAAgVJREFUeF7tWtFtgzAQxUHskU7SRoDEFm1GySRhDCSMSidJBwHcngQ/BHqWjQuGl8/YmLvnd+8O+0Rw8J84uP+BNQBpmn6MQQzDsC6K4ntJcLMsO7dt+zZesyzL3OY91gAkSaLGBgghrraGjdckoJVS9/H/UkorH6weJmMAABiAEDiGBpAKN03zHgQBKfGTGtso8RLPKqWGrFNHUXTTzUJaIpgkCTn8uYSh/7EGgUGZSEpZc+9jAeh3/sEttLVxAiGKogvHBBaAOI7vQoinYmdrDk/Zo5TKq6q6/mWrDgAPIcTZB4fHNhILqqp6sQJgqtDxBYzDA0AbxZXKbAj4zAAAAAYgBKABEEHmwMQoC1CFdTqdvrZUD3Rd9zpVsTphgIsjL1swTY/MjBgAABwceoIBlgisHgJz5/acX0sdn68OwJwBHACcSnPPD+MAwPDiZLEsAAbM7ABHYYSA5d0eNKBHACIIETS7PkcW4FRa9/4faRBpcDoGOYahDthLHcDttOvx1esA1w5y6wOAtQshbodcj4MBYMDKpbBrinPrIwQQAggBo25yo8/ho12OUofo5lpjOVHsx2sp5cW2Tc5bABZplPS5VZZrkiRmsBpAkw7dLD3Ez9bb5cnOvks8/z1kuWlqhB4DdBfzcZ5WCPjomK7NAEAXqb3OAwP2urO6foEBukjtdd4PWjOVX5aRM1gAAAAASUVORK5CYII=";

    @Builder.Default
    private String[] headerTexts = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Builder.Default
    private int tableWidth = 700;

    @Builder.Default
    private int columnWidth = 100;

    @Builder.Default
    private int rowHeight = 50;

    @Builder.Default
    private int dataRowHeight = 20;
    
    @Builder.Default
    private int todayIconSize = 12;

    @Builder.Default
    private Font headerFont = new Font("Default", Font.BOLD, 14);

    @Builder.Default
    private Font contentFont = new Font("Default", Font.PLAIN, 12);
    
    @Builder.Default
    private Font timeFont = new Font("Default", Font.PLAIN, 10);
    
    @Builder.Default
    private int timeMargin = 5;

    /**
     * 生成时间表图片字节数组
     * @param dataList 数据列表
     * @return 图片字节数组
     */
    @SneakyThrows
    public byte[] getByte(List<TimeCell> dataList) {

        dataList = TimeCell.mergeByKeyAndDate(dataList);
        // 分组
        Map<Integer, List<TimeCell>> groupedData = groupDataByWeekday(dataList);
        // 计算最大数据行数
        int maxRows = 0;
        for (int i = 0; i < 7; i++) {
            List<TimeCell> items = groupedData.getOrDefault(i, new ArrayList<>());
            if (items.size() > maxRows) {
                maxRows = items.size();
            }
        }
        // 动态计算表格高度
        int tableHeight = rowHeight + (maxRows * dataRowHeight) + 20; // +20为内容与底部的缓冲
        int imageWidth = tableWidth + marginLeft + marginRight;
        int imageHeight = tableHeight + marginTop + marginBottom;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);

        // 外边框（仅底部和侧边，隐藏header外边框）
        g2d.setColor(outerBorderColor);
        g2d.drawLine(marginLeft, marginTop + tableHeight, marginLeft + tableWidth, marginTop + tableHeight); // 底部边框
        g2d.drawLine(marginLeft, marginTop + rowHeight, marginLeft, marginTop + tableHeight); // 左侧边框从表头下方开始
        g2d.drawLine(marginLeft + tableWidth, marginTop + rowHeight, marginLeft + tableWidth, marginTop + tableHeight); // 右侧边框从表头下方开始
        g2d.drawLine(marginLeft, marginTop + rowHeight, marginLeft + tableWidth, marginTop + rowHeight); // 表头下方的横线（现在使用外边框颜色）
        
        // 内分割线
        g2d.setColor(innerBorderColor);
        // 不再绘制表头下方的横线，因为已经使用外边框颜色绘制了
        for (int i = 1; i < 7; i++) {
            int x = marginLeft + i * columnWidth;
            g2d.drawLine(x, marginTop + rowHeight, x, marginTop + tableHeight); // 内部竖线从表头下方开始
        }
        
        // 获取今天是星期几
        Calendar today = Calendar.getInstance();
        int todayWeekday = today.get(Calendar.DAY_OF_WEEK);
        int todayIndex = todayWeekday == Calendar.SUNDAY ? 6 : todayWeekday - 2;
        
        // 准备todayIcon图片
        BufferedImage todayIconImage = decodeBase64Image(todayIconBase64);
        
        // 表头（居中显示）
        g2d.setFont(headerFont);
        g2d.setColor(headerTextColor);
        for (int i = 0; i < headerTexts.length; i++) {
            // 计算文本宽度以实现居中
            int textWidth = g2d.getFontMetrics().stringWidth(headerTexts[i]);
            int x = marginLeft + i * columnWidth + (columnWidth - textWidth) / 2;
            int y = marginTop + 30;
            g2d.drawString(headerTexts[i], x, y);
            
            // 在今天的星期下方绘制todayIcon
            if (i == todayIndex && todayIconImage != null) {
                int iconX = marginLeft + i * columnWidth + columnWidth / 2 - todayIconSize / 2;
                int iconY = marginTop + rowHeight - todayIconSize - 5;
                g2d.drawImage(todayIconImage, iconX, iconY, todayIconSize, todayIconSize, null);
            }
        }
        
        // 内容（居中显示）
        g2d.setFont(contentFont);
        g2d.setColor(contentTextColor);
        for (int weekday = 0; weekday < 7; weekday++) {
            List<TimeCell> dayItems = groupedData.getOrDefault(weekday, new ArrayList<>());
            for (int i = 0; i < dayItems.size(); i++) {
                TimeCell item = dayItems.get(i);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(item.getDate());
                
                // 计算文本宽度以实现居中
                String text = item.getDesc();
                int textWidth = g2d.getFontMetrics().stringWidth(text);
                int x = marginLeft + weekday * columnWidth + (columnWidth - textWidth) / 2;
                int y = marginTop + rowHeight + 20 + (i * dataRowHeight);
                
                g2d.drawString(text, x, y);
            }
        }
        
        // 添加当前时间到右下角
        drawCurrentTime(g2d, imageWidth, imageHeight);
        
        g2d.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
    
    /**
     * 将Base64字符串解码为BufferedImage
     * @param base64 Base64编码的图片字符串
     * @return 解码后的BufferedImage
     */
    private BufferedImage decodeBase64Image(String base64) {
        try {
            byte[] imageData = Base64.getDecoder().decode(base64);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            return ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在图片右下角绘制当前时间
     * @param g2d Graphics2D对象
     * @param imageWidth 图片宽度
     * @param imageHeight 图片高度
     */
    private void drawCurrentTime(Graphics2D g2d, int imageWidth, int imageHeight) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        
        g2d.setFont(timeFont);
        g2d.setColor(timeTextColor);
        
        // 获取文本尺寸以便正确定位
        int textWidth = g2d.getFontMetrics().stringWidth(currentTime);
        
        // 计算文本位置，距离右下角timeMargin像素
        int x = imageWidth - textWidth - timeMargin;
        int y = imageHeight - timeMargin;
        
        g2d.drawString(currentTime, x, y);
    }

    /**
     * 按星期分组
     */
    private Map<Integer, List<TimeCell>> groupDataByWeekday(List<TimeCell> dataList) {
        Map<Integer, List<TimeCell>> result = new HashMap<>();
        for (TimeCell item : dataList) {
            int weekday = getWeekdayIndex(item.getDate());
            result.computeIfAbsent(weekday, k -> new ArrayList<>()).add(item);
        }
        return result;
    }

    /**
     * 计算日期对应的星期索引
     */
    public static int getWeekdayIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SUNDAY ? 6 : dayOfWeek - 2;
    }
}
