package com.facultyrepository.ui;

import com.facultyrepository.dao.UserDAO;
import com.facultyrepository.model.User;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.SessionManager;
import com.facultyrepository.util.UITheme;
import com.facultyrepository.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Screen 8 — Change Password Form (Dark Mode)
 */
public class ChangePasswordFrame extends JDialog {

    private final UserDAO userDAO = new UserDAO();
    private final User currentUser;

    private JTextField txtUsername;
    private JPasswordField txtCurrentPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private JCheckBox chkShowPasswords;

    private JButton btnChange;
    private JButton btnClear;
    private JButton btnCancel;

    public ChangePasswordFrame(JFrame parent) {
        super(parent, "Change Password - Faculty Research Repository", true);
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initComponents();
        AppIconUtil.applyWindowIcon(this);
    }

    private void initComponents() {
        setSize(520, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.CARD_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Change Account Password");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Ensure account security with a strong password.");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.ACCENT_CYAN);

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(UITheme.CARD_BG);
        headerContent.add(lblTitle);
        headerContent.add(Box.createRigidArea(new Dimension(0, 4)));
        headerContent.add(lblSub);

        header.add(headerContent, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Center Form Card
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(UITheme.BG_DARK);
        centerWrapper.setBorder(new EmptyBorder(20, 24, 16, 24));

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formCard.add(createLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtUsername = new JTextField(currentUser != null ? currentUser.getUsername() : "");
        UITheme.styleTextField(txtUsername);
        txtUsername.setEditable(false);
        txtUsername.setBackground(UITheme.BG_DARK);
        formCard.add(txtUsername, gbc);

        // 2. Current Password
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createLabel("Current Password *:"), gbc);
        gbc.gridx = 1;
        txtCurrentPass = new JPasswordField();
        UITheme.styleTextField(txtCurrentPass);
        formCard.add(txtCurrentPass, gbc);

        // 3. New Password
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createLabel("New Password *:"), gbc);
        gbc.gridx = 1;
        txtNewPass = new JPasswordField();
        UITheme.styleTextField(txtNewPass);
        formCard.add(txtNewPass, gbc);

        // 4. Confirm Password
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(createLabel("Confirm Password *:"), gbc);
        gbc.gridx = 1;
        txtConfirmPass = new JPasswordField();
        UITheme.styleTextField(txtConfirmPass);
        formCard.add(txtConfirmPass, gbc);

        // 5. Show/Hide
        gbc.gridx = 1; gbc.gridy = 4;
        chkShowPasswords = new JCheckBox("Show Passwords");
        chkShowPasswords.setFont(UITheme.FONT_SMALL);
        chkShowPasswords.setBackground(UITheme.CARD_BG);
        chkShowPasswords.setForeground(UITheme.TEXT_MUTED);
        chkShowPasswords.setFocusPainted(false);
        chkShowPasswords.addActionListener(e -> {
            char echo = chkShowPasswords.isSelected() ? (char) 0 : '•';
            txtCurrentPass.setEchoChar(echo);
            txtNewPass.setEchoChar(echo);
            txtConfirmPass.setEchoChar(echo);
        });
        formCard.add(chkShowPasswords, gbc);

        centerWrapper.add(formCard, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottomBar.setBackground(UITheme.BG_DARK);

        btnChange = new JButton("Update Password");
        UITheme.stylePrimaryButton(btnChange);
        btnChange.addActionListener(e -> performPasswordChange());

        btnClear = new JButton("Clear");
        UITheme.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearInputs());

        btnCancel = new JButton("Cancel");
        UITheme.styleDangerButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        bottomBar.add(btnChange);
        bottomBar.add(btnClear);
        bottomBar.add(btnCancel);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    private void performPasswordChange() {
        if (currentUser == null) {
            ValidationUtil.showError(this, "No active user session found!");
            return;
        }

        String curPass = new String(txtCurrentPass.getPassword()).trim();
        String newPass = new String(txtNewPass.getPassword()).trim();
        String confPass = new String(txtConfirmPass.getPassword()).trim();

        if (!ValidationUtil.validatePasswordChange(this, curPass, newPass, confPass)) {
            return;
        }

        try {
            boolean isValidCurPass = userDAO.verifyCurrentPassword(currentUser.getUserId(), curPass);
            if (!isValidCurPass) {
                ValidationUtil.showWarning(this, "Current password is incorrect! Please try again.");
                txtCurrentPass.setText("");
                txtCurrentPass.requestFocus();
                return;
            }

            if (userDAO.updatePassword(currentUser.getUserId(), newPass)) {
                currentUser.setPassword(newPass);
                ValidationUtil.showSuccess(this, "Password updated successfully!");
                dispose();
            } else {
                ValidationUtil.showError(this, "Failed to update password in database.");
            }
        } catch (SQLException ex) {
            ValidationUtil.showError(this, "Database Error: " + ex.getMessage());
        }
    }

    private void clearInputs() {
        txtCurrentPass.setText("");
        txtNewPass.setText("");
        txtConfirmPass.setText("");
        txtCurrentPass.requestFocus();
    }
}
