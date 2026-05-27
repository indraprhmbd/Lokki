package com.lokki.util;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class AppIcon {

    private static final int SIZE = 64;

    private AppIcon() {}

    /**
     * Returns a 64x64 programmatically drawn lock icon for the application window.
     */
    public static ImageIcon getIcon() {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = SIZE / 2;
        int shackleWidth = 20;
        int shackleHeight = 18;
        int shackleArc = 12;
        int shackleLeft = cx - shackleWidth / 2;
        int shackleTop = 8;

        g.setColor(new Color(0x2C, 0x3E, 0x50));
        g.setStroke(new java.awt.BasicStroke(5));
        g.drawArc(shackleLeft, shackleTop, shackleWidth, shackleHeight * 2, 0, 180);

        int bodyLeft = cx - 20;
        int bodyTop = shackleTop + shackleHeight - 2;
        int bodyWidth = 40;
        int bodyHeight = 30;

        GradientPaint gradient = new GradientPaint(0, bodyTop, new Color(0x34, 0x98, 0xDB), 0, bodyTop + bodyHeight, new Color(0x29, 0x80, 0xB9));
        g.setPaint(gradient);
        g.fillRoundRect(bodyLeft, bodyTop, bodyWidth, bodyHeight, 6, 6);

        g.setColor(new Color(0x1A, 0x25, 0x2F));
        g.setStroke(new java.awt.BasicStroke(2));
        g.drawRoundRect(bodyLeft, bodyTop, bodyWidth, bodyHeight, 6, 6);

        int keyholeSize = 8;
        g.setColor(Color.WHITE);
        g.fillOval(cx - keyholeSize / 2, bodyTop + 8, keyholeSize, keyholeSize);
        g.fillRect(cx - 2, bodyTop + 12, 4, 10);

        g.dispose();
        return new ImageIcon(image);
    }
}
