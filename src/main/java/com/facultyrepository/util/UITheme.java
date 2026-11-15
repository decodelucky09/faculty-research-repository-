package com.facultyrepository.util;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Modern Dark Mode & Colorful Theme constants and helper methods.
 * Provides high-contrast, eye-friendly, and professional academic aesthetics.
 */
public class UITheme {

    // 1. Dark Mode Backgrounds
    public static final Color BG_DARK = new Color(11, 15, 25);            // #0B0F19 Space Navy (Main Window)
    public static final Color CARD_BG = new Color(21, 29, 47);            // #151D2F Elevated Slate Card
    public static final Color SURFACE_BG = new Color(30, 41, 59);         // #1E293B Inputs / Toolbars
    public static final Color BORDER_COLOR = new Color(46, 58, 89);       // #2E3A59 Border Line
    public static final Color BORDER_FOCUS = new Color(99, 102, 241);     // #6366F1 Focus Glow

    // 2. Vibrant Colorful Accents
    public static final Color PRIMARY_COLOR = new Color(99, 102, 241);    // #6366F1 Electric Indigo
    public static final Color PRIMARY_HOVER = new Color(79, 70, 229);     // #4F46E5
    public static final Color ACCENT_CYAN = new Color(6, 182, 212);       // #06B6D4 Vibrant Cyan
    public static final Color ACCENT_PURPLE = new Color(168, 85, 247);    // #A855F7 Vibrant Violet
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129);    // #10B981 Emerald Green
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);     // #059669
    public static final Color DANGER_COLOR = new Color(244, 63, 94);      // #F43F5E Vivid Rose Crimson
    public static final Color DANGER_HOVER = new Color(225, 29, 72);      // #E11D48
    public static final Color WARNING_COLOR = new Color(245, 158, 11);    // #F59E0B Radiant Amber Gold

    // 3. Crisp Typography Colors
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);    // #F8FAFC Pure Crisp White
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);  // #94A3B8 Cool Slate Gray
    public static final Color TEXT_MUTED = new Color(100, 116, 139);      // #64748B Subdued Gray

    // 4. Table Colors
    public static final Color TABLE_ROW_EVEN = new Color(17, 24, 39);     // #111827 Dark
    public static final Color TABLE_ROW_ODD = new Color(22, 32, 50);      // #162032 Alternate
    public static final Color TABLE_HEADER_BG = new Color(30, 41, 59);    // #1E293B Header
    public static final Color TABLE_SELECTION = new Color(67, 56, 202);   // #4338CA Royal Indigo Selection
    public static final Color TABLE_GRID = new Color(36, 48, 72);         // #243048

    // 5. Typography Font Definitions
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_METRIC_VALUE = new Font(FONT_FAMILY, Font.BOLD, 30);

    /**
     * Styles a primary button (Electric Indigo).
     */
    public static void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BOLD);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY_COLOR, 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
    }

    /**
     * Styles a secondary button (Dark Slate with Blue Border).
     */
    public static void styleSecondaryButton(JButton button) {
        button.setFont(FONT_BOLD);
        button.setBackground(SURFACE_BG);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
    }

    /**
     * Styles a success button (Emerald Green).
     */
    public static void styleSuccessButton(JButton button) {
        button.setFont(FONT_BOLD);
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(SUCCESS_COLOR, 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
    }

    /**
     * Styles a danger button (Rose Crimson).
     */
    public static void styleDangerButton(JButton button) {
        button.setFont(FONT_BOLD);
        button.setBackground(DANGER_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(DANGER_COLOR, 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
    }

    /**
     * Styles text fields for Dark Mode.
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_REGULAR);
        textField.setForeground(TEXT_PRIMARY);
        textField.setBackground(SURFACE_BG);
        textField.setCaretColor(PRIMARY_COLOR);
        textField.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(7, 12, 7, 12)
        ));
    }

    /**
     * Styles combo boxes for Dark Mode.
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_REGULAR);
        comboBox.setBackground(SURFACE_BG);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }

    /**
     * Styles a JTable for Dark Mode with alternating row colors and clear headers.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(30);
        table.setBackground(TABLE_ROW_EVEN);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(TABLE_GRID);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(ACCENT_CYAN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
        header.setBorder(new LineBorder(BORDER_COLOR, 1));

        // Custom cell renderer for alternating row colors
        DefaultTableCellRenderer alternatingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                if (isSel) {
                    comp.setBackground(TABLE_SELECTION);
                    comp.setForeground(Color.WHITE);
                } else {
                    comp.setBackground((r % 2 == 0) ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    comp.setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return comp;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(alternatingRenderer);
        }
    }

    /**
     * Creates a styled elevated card panel.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));
        return card;
    }
}
