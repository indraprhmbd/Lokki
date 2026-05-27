package com.lokki.view.component;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PasswordStrengthBar extends JComponent {

    public enum Strength {
        WEAK("Weak", new Color(220, 53, 69)),
        MEDIUM("Medium", new Color(255, 193, 7)),
        STRONG("Strong", new Color(40, 167, 69)),
        VERY_STRONG("Very Strong", new Color(0, 123, 255));

        private final String label;
        private final Color color;

        Strength(String label, Color color) {
            this.label = label;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public Color getColor() {
            return color;
        }
    }

    private Strength currentStrength = Strength.WEAK;
    private float fillRatio = 0.0f;

    public PasswordStrengthBar() {
        setPreferredSize(new Dimension(200, 18));
        setMinimumSize(new Dimension(100, 18));
    }

    /**
     * Evaluates password strength based on length and character variety.
     */
    public static Strength evaluateStrength(char[] password) {
        if (password == null || password.length == 0) {
            return Strength.WEAK;
        }

        int length = password.length;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        for (char c : password) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSymbol = true;
        }

        int typeCount = 0;
        if (hasUpper) typeCount++;
        if (hasLower) typeCount++;
        if (hasDigit) typeCount++;
        if (hasSymbol) typeCount++;

        if (length >= 16 && typeCount >= 3) return Strength.VERY_STRONG;
        if (length >= 12) return Strength.STRONG;
        if (length >= 8) return Strength.MEDIUM;
        return Strength.WEAK;
    }

    /**
     * Updates the bar to reflect the given password's strength.
     */
    public void updateStrength(char[] password) {
        currentStrength = evaluateStrength(password);
        switch (currentStrength) {
            case WEAK: fillRatio = 0.25f; break;
            case MEDIUM: fillRatio = 0.5f; break;
            case STRONG: fillRatio = 0.75f; break;
            case VERY_STRONG: fillRatio = 1.0f; break;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int barHeight = height - 4;

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRoundRect(0, 2, width, barHeight, 4, 4);

        int fillWidth = (int) (width * fillRatio);
        if (fillWidth > 0) {
            g2.setColor(currentStrength.getColor());
            g2.fillRoundRect(0, 2, fillWidth, barHeight, 4, 4);
        }

        g2.setColor(Color.DARK_GRAY);
        String text = currentStrength.getLabel();
        int textX = 6;
        int textY = barHeight / 2 + g2.getFontMetrics().getAscent() / 2 + 1;
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
