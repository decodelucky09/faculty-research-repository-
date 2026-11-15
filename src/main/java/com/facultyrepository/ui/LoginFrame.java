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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Screen 1 — Login Frame (Dark Mode & Branded with Professional Logo)
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JButton btnExit;

    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        initComponents();
        AppIconUtil.applyWindowIcon(this);
    }

    private void initComponents() {
        setTitle("Sign In - Faculty Research Publication Repository");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 630);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header Banner with Logo
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.CARD_BG);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(24, 20, 20, 20));

        // Display Scaled Professional Logo
        ImageIcon logoIcon = AppIconUtil.getScaledLogo(85, 85);
        if (logoIcon != null) {
            JLabel lblLogo = new JLabel(logoIcon);
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(lblLogo);
            headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JLabel lblAppTitle = new JLabel("Faculty Publication Repository");
        lblAppTitle.setFont(UITheme.FONT_TITLE);
        lblAppTitle.setForeground(UITheme.TEXT_PRIMARY);
        lblAppTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Academic Research & Accreditation Management");
        lblSubTitle.setFont(UITheme.FONT_REGULAR);
        lblSubTitle.setForeground(UITheme.ACCENT_CYAN);
        lblSubTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblAppTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSubTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Center Form Card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UITheme.BG_DARK);
        centerWrapper.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card Subtitle
        JLabel lblLoginHeader = new JLabel("Enter Credentials to Access Portal");
        lblLoginHeader.setFont(UITheme.FONT_SUBTITLE);
        lblLoginHeader.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formCard.add(lblLoginHeader, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.TEXT_SECONDARY);
        formCard.add(lblUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        txtUsername = new JTextField(20);
        UITheme.styleTextField(txtUsername);
        txtUsername.setText("admin"); // convenient default for viva/demonstration
        formCard.add(txtUsername, gbc);

        // Password
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.TEXT_SECONDARY);
        formCard.add(lblPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        txtPassword = new JPasswordField(20);
        UITheme.styleTextField(txtPassword);
        txtPassword.setText("admin123"); // convenient default for viva/demonstration
        formCard.add(txtPassword, gbc);

        // Show/Hide Checkbox
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(UITheme.FONT_SMALL);
        chkShowPassword.setBackground(UITheme.CARD_BG);
        chkShowPassword.setForeground(UITheme.TEXT_MUTED);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '•');
        });
        formCard.add(chkShowPassword, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(UITheme.CARD_BG);

        btnLogin = new JButton("Sign In");
        UITheme.stylePrimaryButton(btnLogin);
        btnLogin.setPreferredSize(new Dimension(110, 38));

        btnClear = new JButton("Clear");
        UITheme.styleSecondaryButton(btnClear);
        btnClear.setPreferredSize(new Dimension(90, 38));

        btnExit = new JButton("Exit");
        UITheme.styleDangerButton(btnExit);
        btnExit.setPreferredSize(new Dimension(90, 38));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExit);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 12, 6, 12);
        formCard.add(buttonPanel, gbc);

        // Credentials Hint
        JLabel lblHint = new JLabel("<html><center><font color='#94A3B8' size='2'>" +
                "Test Accounts: <font color='#38BDF8'><b>admin / admin123</b></font> (Admin) &bull; " +
                "<font color='#34D399'><b>john_doe / user123</b></font> (User)" +
                "</font></center></html>");
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 12, 4, 12);
        formCard.add(lblHint, gbc);

        centerWrapper.add(formCard);
        add(centerWrapper, BorderLayout.CENTER);

        // Enter key listeners
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyListener);
        txtPassword.addKeyListener(enterKeyListener);

        btnLogin.addActionListener(e -> performLogin());
        btnClear.addActionListener(e -> clearFields());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (!ValidationUtil.validateRequired(this, username, "Username")) {
            txtUsername.requestFocus();
            return;
        }
        if (!ValidationUtil.validateRequired(this, password, "Password")) {
            txtPassword.requestFocus();
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                dispose();

                if (user.isAdmin()) {
                    new AdminDashboard().setVisible(true);
                } else {
                    new UserDashboard().setVisible(true);
                }
            } else {
                ValidationUtil.showError(this, "Invalid Username or Password!\nPlease check your credentials and try again.");
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (SQLException ex) {
            ValidationUtil.showError(this,
                    "Database Connection Failed!\n" +
                    "Error: " + ex.getMessage() + "\n\n" +
                    "Ensure MySQL service is running and 'faculty_research_repository' database is imported.");
        }
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }
}
