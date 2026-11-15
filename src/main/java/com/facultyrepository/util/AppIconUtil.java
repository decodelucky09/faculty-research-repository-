package com.facultyrepository.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Utility to manage and scale the application logo and window icons.
 */
public class AppIconUtil {

    private static Image cachedLogo;

    public static Image getAppLogo() {
        if (cachedLogo != null) return cachedLogo;
        try (InputStream is = AppIconUtil.class.getClassLoader().getResourceAsStream("logo.jpg")) {
            if (is != null) {
                cachedLogo = ImageIO.read(is);
                return cachedLogo;
            }
        } catch (Exception ignore) {
        }
        // Fallback: procedurally generated high-res vector icon
        cachedLogo = createFallbackLogo(128);
        return cachedLogo;
    }

    public static ImageIcon getScaledLogo(int width, int height) {
        Image img = getAppLogo();
        if (img != null) {
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }

    public static void applyWindowIcon(Window window) {
        Image img = getAppLogo();
        if (img != null) {
            window.setIconImage(img);
        }
    }

    private static Image createFallbackLogo(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Circular gradient background
        GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241), size, size, new Color(15, 23, 42));
        g2.setPaint(gp);
        g2.fill(new Ellipse2D.Double(2, 2, size - 4, size - 4));

        // Gold border
        g2.setColor(new Color(245, 158, 11));
        g2.setStroke(new BasicStroke(3));
        g2.draw(new Ellipse2D.Double(2, 2, size - 4, size - 4));

        // Letters FR
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, size / 3));
        FontMetrics fm = g2.getFontMetrics();
        String text = "FR";
        int tx = (size - fm.stringWidth(text)) / 2;
        int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);

        g2.dispose();
        return img;
    }
}
