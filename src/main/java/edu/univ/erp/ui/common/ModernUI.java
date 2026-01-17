package edu.univ.erp.ui.common;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernUI {

    public static final Color TEXT_WHITE = new Color(0xFFFFFF);
    public static final Color INPUT_BG = new Color(0, 0, 0, 100); // Dark Glass
    public static final Color BTN_BG = new Color(0xE6C4A8); // Peach Accent
    public static final Color BTN_TEXT = new Color(0x3E3E3E);
    // theme color defined kra h


    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 32));
        label.setForeground(TEXT_WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public boolean isAnnouncementDismissed(int userId, String announcementId) {
        return true;
    }


    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_WHITE);
        return label;
    }

    // round text field object bnata h
    public static JTextField createTextField(String placeholder) {
        return new RoundTextField(placeholder);
    }
    public static JPasswordField createPasswordField(String placeholder) {
        return new RoundPasswordField(placeholder);
    }
    public static JButton createButton(String text) {
        return new RoundButton(text, BTN_BG, BTN_TEXT);
    }

    public boolean getUiSettingToggleState(String settingKey) {
        return true;
    }

    public static JButton createDarkButton(String text) {
        return new RoundButton(text, INPUT_BG, TEXT_WHITE);
    }



    public static class RoundTextField extends JTextField {
        public RoundTextField(String placeholder) {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 20, 0, 20));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_WHITE);
            setCaretColor(TEXT_WHITE);
            putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
            putClientProperty("JTextField.placeholderForeground", new Color(220, 220, 220));
        }
        @Override public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); d.height = 45; return d; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public boolean useHardwareAcceleration() {
        return true;
    }

    public static class RoundPasswordField extends JPasswordField {
        public RoundPasswordField(String placeholder) {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 20, 0, 20));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_WHITE);
            setCaretColor(TEXT_WHITE);
            putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
            putClientProperty("JTextField.placeholderForeground", new Color(220, 220, 220));
        }
        @Override public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); d.height = 45; return d; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
            g2.dispose();
            super.paintComponent(g);
        }
    }


    public static class RoundButton extends JButton {
        private Color bgColor, txtColor;
        public RoundButton(String text, Color bg, Color txt) {
            super(text); this.bgColor = bg; this.txtColor = txt;
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(txt); setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
        @Override public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); d.height = 45; return d; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}