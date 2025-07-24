package com.chxt.domain.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WriteFontUtils {

    /**
     * 在指定画布上指定位置绘制艺术字
     * @param g2d 画布
     * @param x x坐标
     * @param y y坐标
     * @param text 文字内容
     */
    /**
     * 在指定画布上指定位置绘制艺术字，可指定字体大小
     * @param g2d 画布
     * @param x x坐标
     * @param y y坐标
     * @param text 文字内容
     * @param size 字体大小
     */
    public static void typeA(Graphics2D g2d, double x, double y, String text, int size) {
        Font font = new Font("微软雅黑", Font.BOLD, size);
        g2d.setFont(font);

        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, text);
        Shape textShape = gv.getOutline((float) x, (float) y);

        // 阴影
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.translate(4, 4);
        g2d.fill(textShape);
        g2d.translate(-4, -4);

        // 渐变填充
        GradientPaint gradient = new GradientPaint(0, 0, Color.CYAN, 400, 200, Color.MAGENTA);
        g2d.setPaint(gradient);
        g2d.fill(textShape);

        // 轮廓描边
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(textShape);
    }

  public static void main(String[] args) throws IOException {
        String text = "陈心桐";
        int width = 400;
        int height = 200;

        // 创建画布
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 开启抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算文字位置
        Font font = new Font("微软雅黑", Font.BOLD, 80);
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, text);
        Rectangle2D bounds = gv.getVisualBounds();
        double x = (width - bounds.getWidth()) / 2 - bounds.getX();
        double y = (height - bounds.getHeight()) / 2 - bounds.getY();

        // 背景透明，清空画布
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);

        // 调用 typeA 方法绘制艺术字
        typeA(g2d, x, y, text, 80);

        g2d.dispose();

        // 保存图片
        ImageIO.write(image, "png", new File("artistic_text.png"));
        System.out.println("已生成艺术字图片: artistic_text.png");
    }
}