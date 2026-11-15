package com.facultyrepository;

import com.facultyrepository.db.DatabaseConnection;
import com.facultyrepository.ui.LoginFrame;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.UITheme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Main Application Entry Point
 * Configures professional dark mode Look-and-Feel, verifies database connectivity,
 * and launches the Login screen.
 */
public class Main {

    public static void main(String[] args) {
        // Configure Modern Dark Theme Defaults in Swing UIManager
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
            Font boldFont = new Font("Segoe UI", Font.BOLD, 13);

            // Global Typography
            UIManager.put("Button.font", boldFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("PasswordField.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("TextArea.font", defaultFont);

            // Dark Mode Dialogs & OptionPanes
            UIManager.put("Panel.background", new ColorUIResource(UITheme.CARD_BG));
            UIManager.put("OptionPane.background", new ColorUIResource(UITheme.CARD_BG));
            UIManager.put("OptionPane.messageForeground", new ColorUIResource(UITheme.TEXT_PRIMARY));
            UIManager.put("OptionPane.messageFont", defaultFont);
            UIManager.put("OptionPane.buttonFont", boldFont);

        } catch (Exception e) {
            System.err.println("Could not configure Look and Feel: " + e.getMessage());
        }

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            boolean isDbConnected = DatabaseConnection.testConnection();
            if (!isDbConnected) {
                String warningMsg =
                        "<html><b>Notice: Could not connect to MySQL database!</b><br><br>" +
                        "Database: <code>faculty_research_repository</code><br>" +
                        "URL: <code>" + DatabaseConnection.getDbUrl() + "</code><br><br>" +
                        "Before logging in, please ensure:<br>" +
                        "1. MySQL Server is running (e.g. MySQL80 service).<br>" +
                        "2. Database schema has been imported.<br>" +
                        "3. Credentials in <code>src/main/resources/db.properties</code> match.<br><br>" +
                        "Click <b>OK</b> to continue to the application.</html>";

                JOptionPane.showMessageDialog(
                        null,
                        warningMsg,
                        "Database Notice",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            LoginFrame loginFrame = new LoginFrame();
            AppIconUtil.applyWindowIcon(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}
