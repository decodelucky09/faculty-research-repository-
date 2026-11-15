package com.facultyrepository.util;

import com.facultyrepository.dao.FacultyDAO;
import com.facultyrepository.dao.PublicationDAO;
import com.facultyrepository.dao.UserDAO;
import com.facultyrepository.model.Faculty;
import com.facultyrepository.model.Publication;
import com.facultyrepository.model.User;
import com.facultyrepository.ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Automates high-resolution screenshot generation directly from actual working UI components.
 */
public class ProjectScreenshotGenerator {

    private static final String OUTPUT_DIR = "screenshots/";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

                new File(OUTPUT_DIR).mkdirs();

                // Authenticate Session as Lucky Saini (Admin)
                UserDAO userDAO = new UserDAO();
                User user = userDAO.authenticate("admin", "admin123");
                if (user == null) {
                    user = new User(1, "admin", "admin123", "Admin", "Lucky Saini (Admin)", "admin@university.edu");
                }
                SessionManager.getInstance().setCurrentUser(user);

                PublicationDAO publicationDAO = new PublicationDAO();
                Publication samplePub = publicationDAO.getPublicationById(1);

                System.out.println("Starting Screenshot Generation...");

                // 1. Figure 5.1: Login Page
                captureLoginScreen();

                // 2. Figure 5.2: Home / Dashboard
                captureDashboardScreen();

                // 3. Figure 5.3: Faculty Registration Page
                captureFacultyRegistrationScreen();

                // 4. Figure 5.4: Add Publication Page
                captureAddPublicationScreen();

                // 5. Figure 5.5: Publication Repository
                capturePublicationRepositoryScreen();

                // 6. Figure 5.6: Search Publication Page
                captureSearchPublicationScreen();

                // 7. Figure 5.7: Update Publication Page
                captureUpdatePublicationScreen(samplePub);

                // 8. Figure 5.8: Delete Publication Page
                captureDeletePublicationScreen();

                // 9. Figure 5.9: Faculty Profile / Details
                captureFacultyProfileScreen();

                // 10. Figure 5.10: Settings / Change Password
                captureChangePasswordScreen();

                // 11. Figure 5.11: Database Record Details
                capturePublicationDetailsScreen(samplePub);

                System.out.println("All screenshots generated successfully in: " + new File(OUTPUT_DIR).getAbsolutePath());
                System.exit(0);

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void saveComponent(Component comp, String filename) {
        try {
            int w = comp.getWidth();
            int h = comp.getHeight();
            if (w <= 0 || h <= 0) {
                Dimension pref = comp.getPreferredSize();
                w = pref.width > 0 ? pref.width : 800;
                h = pref.height > 0 ? pref.height : 600;
                comp.setSize(w, h);
            }
            comp.validate();
            comp.doLayout();

            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            comp.printAll(g2);
            g2.dispose();

            File out = new File(OUTPUT_DIR + filename);
            ImageIO.write(img, "png", out);
            System.out.println("Captured: " + filename + " (" + w + "x" + h + ") - " + out.length() + " bytes");
        } catch (Exception e) {
            System.err.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

    private static void captureLoginScreen() {
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
        try { Thread.sleep(400); } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_1_Login_Page.png");
        frame.dispose();
    }

    private static void captureDashboardScreen() {
        AdminDashboard frame = new AdminDashboard();
        frame.setVisible(true);
        try { Thread.sleep(500); } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_2_Home_Dashboard.png");
        frame.dispose();
    }

    private static void captureFacultyRegistrationScreen() {
        FacultyFrame frame = new FacultyFrame(null);
        frame.setVisible(true);
        try {
            Thread.sleep(400);
            // Pre-fill form fields using reflection to showcase Registration input
            setFieldValue(frame, "txtEmployeeId", "FAC-CSE-007");
            setFieldValue(frame, "txtFacultyName", "Dr. Ramesh Verma");
            setFieldValue(frame, "txtEmail", "ramesh.verma@university.edu");
            setFieldValue(frame, "txtPhone", "+91 9876543220");
            setFieldValue(frame, "txtSpecialization", "Cyber Security & Cloud Forensics");
            frame.validate();
            frame.repaint();
            Thread.sleep(200);
        } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_3_Faculty_Registration_Page.png");
        frame.dispose();
    }

    private static void captureAddPublicationScreen() {
        AddPublicationFrame frame = new AddPublicationFrame(null, null);
        frame.setVisible(true);
        try {
            Thread.sleep(400);
            setFieldValue(frame, "txtTitle", "Edge-AI Architectures for Low-Latency Industrial Automation");
            setFieldValue(frame, "txtJournalConference", "IEEE Transactions on Industrial Informatics");
            setFieldValue(frame, "txtResearchArea", "Artificial Intelligence & Edge Computing");
            setFieldValue(frame, "txtYear", "2026");
            setFieldValue(frame, "txtDoi", "10.1109/TII.2026.3401122");
            setFieldValue(frame, "txtIssnIsbn", "1551-3203");
            setFieldValue(frame, "txtPublisher", "IEEE");
            setFieldValue(frame, "txtLink", "https://doi.org/10.1109/TII.2026.3401122");
            setFieldText(frame, "txtAbstract", "This research develops an optimized edge-computing inference pipeline for real-time sensor streams in autonomous industrial robotics.");
            frame.validate();
            frame.repaint();
            Thread.sleep(200);
        } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_4_Add_Publication_Page.png");
        frame.dispose();
    }

    private static void capturePublicationRepositoryScreen() {
        PublicationFrame frame = new PublicationFrame(null);
        frame.setVisible(true);
        try { Thread.sleep(500); } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_5_Publication_Repository.png");
        frame.dispose();
    }

    private static void captureSearchPublicationScreen() {
        SearchPublicationFrame frame = new SearchPublicationFrame(null);
        frame.setVisible(true);
        try {
            Thread.sleep(500);
            setFieldValue(frame, "txtTitle", "Deep");
            // Trigger search
            invokeMethod(frame, "executeSearch");
            Thread.sleep(300);
        } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_6_Search_Publication_Page.png");
        frame.dispose();
    }

    private static void captureUpdatePublicationScreen(Publication samplePub) {
        AddPublicationFrame frame = new AddPublicationFrame(null, samplePub);
        frame.setVisible(true);
        try { Thread.sleep(400); } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_7_Update_Publication_Page.png");
        frame.dispose();
    }

    private static void captureDeletePublicationScreen() {
        PublicationFrame frame = new PublicationFrame(null);
        frame.setVisible(true);
        try {
            Thread.sleep(500);
            selectTableRow(frame.getContentPane(), 3);
            Thread.sleep(200);

            JRootPane root = frame.getRootPane();
            BufferedImage frameImg = new BufferedImage(root.getWidth(), root.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = frameImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            root.printAll(g2);

            // Create and composite actual delete confirmation modal dialog
            JOptionPane optPane = new JOptionPane(
                    "Are you sure you want to delete publication:\n\"Deep Transformer Architectures for Autonomous Decision Systems\"?",
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION
            );
            JDialog dlg = optPane.createDialog(frame, "Confirm Delete Publication");
            dlg.setSize(540, 180);
            dlg.setLocationRelativeTo(frame);
            dlg.validate();

            BufferedImage dlgImg = new BufferedImage(dlg.getWidth(), dlg.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D gDlg = dlgImg.createGraphics();
            gDlg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dlg.getRootPane().printAll(gDlg);
            gDlg.dispose();

            int dx = (frameImg.getWidth() - dlg.getWidth()) / 2;
            int dy = (frameImg.getHeight() - dlg.getHeight()) / 2;
            g2.drawImage(dlgImg, dx, dy, null);
            g2.dispose();

            File out = new File(OUTPUT_DIR + "Figure_5_8_Delete_Publication_Page.png");
            ImageIO.write(frameImg, "png", out);
            System.out.println("Captured: Figure_5_8_Delete_Publication_Page.png with Confirmation Dialog");
            dlg.dispose();
        } catch (Exception ignore) {}
        frame.dispose();
    }

    private static void captureFacultyProfileScreen() {
        FacultyFrame frame = new FacultyFrame(null);
        frame.setVisible(true);
        try {
            Thread.sleep(500);
            selectTableRow(frame.getContentPane(), 0);
            Thread.sleep(200);
        } catch (Exception ignore) {}
        saveComponent(frame.getRootPane(), "Figure_5_9_Faculty_Profile_Details.png");
        frame.dispose();
    }

    private static void captureChangePasswordScreen() {
        ChangePasswordFrame dialog = new ChangePasswordFrame(null);
        dialog.setModal(false);
        dialog.setVisible(true);
        try {
            Thread.sleep(400);
            setFieldValue(dialog, "txtCurrentPass", "admin123");
            setFieldValue(dialog, "txtNewPass", "newpass2026");
            setFieldValue(dialog, "txtConfirmPass", "newpass2026");
            dialog.validate();
            dialog.repaint();
            Thread.sleep(200);
        } catch (Exception ignore) {}
        saveComponent(dialog.getRootPane(), "Figure_5_10_Settings_Change_Password.png");
        dialog.dispose();
    }

    private static void capturePublicationDetailsScreen(Publication samplePub) {
        if (samplePub != null) {
            PublicationDetailsFrame dialog = new PublicationDetailsFrame(null, samplePub);
            dialog.setModal(false);
            dialog.setVisible(true);
            try { Thread.sleep(400); } catch (Exception ignore) {}
            saveComponent(dialog.getRootPane(), "Figure_5_11_Database_Record_Details.png");
            dialog.dispose();
        }
    }

    private static void setFieldValue(Object target, String fieldName, String value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object obj = f.get(target);
            if (obj instanceof JTextField) {
                ((JTextField) obj).setText(value);
            }
        } catch (Exception ignore) {}
    }

    private static void setFieldText(Object target, String fieldName, String value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object obj = f.get(target);
            if (obj instanceof JTextArea) {
                ((JTextArea) obj).setText(value);
            }
        } catch (Exception ignore) {}
    }

    private static void invokeMethod(Object target, String methodName) {
        try {
            java.lang.reflect.Method m = target.getClass().getDeclaredMethod(methodName);
            m.setAccessible(true);
            m.invoke(target);
        } catch (Exception ignore) {}
    }

    private static void selectTableRow(Component comp, int rowIndex) {
        if (comp instanceof JTable) {
            JTable tbl = (JTable) comp;
            if (tbl.getRowCount() > rowIndex) {
                tbl.setRowSelectionInterval(rowIndex, rowIndex);
                tbl.setColumnSelectionInterval(0, tbl.getColumnCount() - 1);
                tbl.changeSelection(rowIndex, 0, false, false);
                tbl.repaint();
            }
        } else if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                selectTableRow(child, rowIndex);
            }
        }
    }
}
