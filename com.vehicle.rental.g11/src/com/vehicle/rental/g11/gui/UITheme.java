package com.vehicle.rental.g11.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UITheme {
    // Base colours (shared with MainFrame)
    public static final Color BG = Color.WHITE;
    public static final Color PURPLE = new Color(120, 60, 200);
    // Semantic colours
    public static final Color LOGOUT = new Color(210, 50, 50);
    public static final Color LOGOUT_HOVER = new Color(180, 30, 30);
    public static final Color SUCCESS = new Color(34, 139, 34);
    public static final Color INFO = new Color(30, 30, 180);
    public static final Color WARNING = new Color(180, 30, 30);
    // Dimensions
    public static final Dimension NAV_BTN_SIZE = new Dimension(220, 164);
    public static final Dimension ACTION_BTN_SIZE = new Dimension(140, 40);
    // Fonts
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_LABEL = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 16);
    public static final Color PURPLE_HOVER = new Color(100, 40, 180);
    public static final Color PURPLE_LIGHT = new Color(245, 240, 255);

    // Login / accent palette (mirrors LoginFrame constants)
    public static final Color ACCENT = PURPLE; // alias for consistency
    public static final Color ACCENT_HOVER = PURPLE_HOVER;
    public static final Color BG_CARD = new Color(250, 248, 255);
    public static final Color TEXT_PRIMARY = new Color(30, 10, 60);
    public static final Color TEXT_MUTED = new Color(130, 100, 170);
    public static final Color FIELD_BG = new Color(245, 240, 255);
    public static final Color FIELD_BORDER = new Color(200, 180, 230);
    
    // Header subtext colour (used in MainFrame header)
    public static final Color HEADER_SUBTEXT = new Color(210, 190, 255);

    /**
     * Creates a rounded button that follows the application's purple accent style.
     * The button uses anti‑aliased painting, white text, a bold Arial font and a hand cursor.
     *
     * @param text the button label
     * @return a {@link JButton} styled as a navigation button
     */
    public static JButton roundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PURPLE_HOVER : PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a rounded button with custom background and hover colours.
     *
     * @param text   the button label
     * @param bg     background colour
     * @param hover  hover colour
     * @return a styled {@link JButton}
     */
    public static JButton roundedButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Styles a JTextField (or JPasswordField) to match the theme.
     */
    public static void styleField(JTextField f) {
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(FIELD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 32));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(7, 10, 7, 10)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(FIELD_BORDER, 1, true),
                        BorderFactory.createEmptyBorder(7, 10, 7, 10)
                ));
            }
        });
    }

    /**
     * Applies theme colors to a JTable.
     */
    public static void styleTable(JTable table) {
        // Basic table styling to match the theme
        table.setBackground(BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_LABEL);
        table.setSelectionBackground(PURPLE);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        if (table.getTableHeader() != null) {
            table.getTableHeader().setBackground(PURPLE);
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(FONT_LABEL);
        }
    }

    /**
     * Styles a JComboBox to match the theme.
     */
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(FIELD_BG);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_LABEL);
        cb.setBorder(null);
    }

}