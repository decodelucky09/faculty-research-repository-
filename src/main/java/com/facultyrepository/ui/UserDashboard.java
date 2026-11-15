package com.facultyrepository.ui;

import com.facultyrepository.dao.FacultyDAO;
import com.facultyrepository.dao.PublicationDAO;
import com.facultyrepository.model.Publication;
import com.facultyrepository.model.User;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.SessionManager;
import com.facultyrepository.util.UITheme;
import com.facultyrepository.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

/**
 * Screen 2b — User Dashboard (Dark Mode & Faculty Portal)
 */
public class UserDashboard extends JFrame {

    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final PublicationDAO publicationDAO = new PublicationDAO();

    private JLabel lblTotalFaculty;
    private JLabel lblTotalPublications;
    private JLabel lblCurrentYearPubs;
    private JLabel lblTotalDepts;
    private JTable tblRecentPubs;
    private DefaultTableModel tableModel;

    public UserDashboard() {
        initComponents();
        AppIconUtil.applyWindowIcon(this);
        loadDashboardData();
    }

    private void initComponents() {
        User user = SessionManager.getInstance().getCurrentUser();
        String displayName = user != null ? user.getFullName() : "Faculty User";

        setTitle("Faculty Portal - Research Publication Repository");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        add(createToolBar(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UITheme.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Welcome Banner
        JPanel welcomeCard = UITheme.createCardPanel();
        welcomeCard.setLayout(new BorderLayout(14, 0));
        welcomeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        ImageIcon miniLogo = AppIconUtil.getScaledLogo(54, 54);
        if (miniLogo != null) {
            JLabel lblLogo = new JLabel(miniLogo);
            welcomeCard.add(lblLogo, BorderLayout.WEST);
        }

        JPanel welcomeText = new JPanel();
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.setBackground(UITheme.CARD_BG);

        JLabel lblGreeting = new JLabel("Welcome, " + displayName);
        lblGreeting.setFont(UITheme.FONT_TITLE);
        lblGreeting.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Role: Faculty Member | Academic Research Explorer");
        lblSubtitle.setFont(UITheme.FONT_REGULAR);
        lblSubtitle.setForeground(UITheme.ACCENT_CYAN);

        welcomeText.add(lblGreeting);
        welcomeText.add(Box.createRigidArea(new Dimension(0, 4)));
        welcomeText.add(lblSubtitle);
        welcomeCard.add(welcomeText, BorderLayout.CENTER);

        JButton btnHeaderLogout = new JButton("Logout");
        UITheme.styleDangerButton(btnHeaderLogout);
        btnHeaderLogout.addActionListener(e -> performLogout());
        welcomeCard.add(btnHeaderLogout, BorderLayout.EAST);

        mainPanel.add(welcomeCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Metric Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(UITheme.BG_DARK);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        lblTotalFaculty = new JLabel("0", SwingConstants.CENTER);
        lblTotalPublications = new JLabel("0", SwingConstants.CENTER);
        lblCurrentYearPubs = new JLabel("0", SwingConstants.CENTER);
        lblTotalDepts = new JLabel("0", SwingConstants.CENTER);

        int curYear = Year.now().getValue();
        statsPanel.add(createMetricCard("Total Faculty", lblTotalFaculty, UITheme.PRIMARY_COLOR));
        statsPanel.add(createMetricCard("Total Publications", lblTotalPublications, UITheme.ACCENT_CYAN));
        statsPanel.add(createMetricCard("Publications (" + curYear + ")", lblCurrentYearPubs, UITheme.SUCCESS_COLOR));
        statsPanel.add(createMetricCard("Departments", lblTotalDepts, UITheme.WARNING_COLOR));

        mainPanel.add(statsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. Quick Action Buttons
        JPanel actionCard = UITheme.createCardPanel();
        actionCard.setLayout(new BorderLayout());
        actionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel lblNavTitle = new JLabel("Available Actions:");
        lblNavTitle.setFont(UITheme.FONT_HEADER);
        lblNavTitle.setForeground(UITheme.TEXT_SECONDARY);
        actionCard.add(lblNavTitle, BorderLayout.WEST);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navButtons.setBackground(UITheme.CARD_BG);

        JButton btnViewPubs = new JButton("Browse All Publications");
        UITheme.stylePrimaryButton(btnViewPubs);
        btnViewPubs.addActionListener(e -> openPublicationManagement());

        JButton btnSearchPubs = new JButton("Advanced Search & Filter");
        UITheme.styleSuccessButton(btnSearchPubs);
        btnSearchPubs.addActionListener(e -> openSearchPublications());

        JButton btnChangePass = new JButton("Change Password");
        UITheme.styleSecondaryButton(btnChangePass);
        btnChangePass.addActionListener(e -> openChangePassword());

        navButtons.add(btnViewPubs);
        navButtons.add(btnSearchPubs);
        navButtons.add(btnChangePass);
        actionCard.add(navButtons, BorderLayout.EAST);

        mainPanel.add(actionCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4. Recent Publications JTable
        JPanel recentPanel = UITheme.createCardPanel();
        recentPanel.setLayout(new BorderLayout(0, 10));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setBackground(UITheme.CARD_BG);

        JLabel lblRecentTitle = new JLabel("Recent Research Publications");
        lblRecentTitle.setFont(UITheme.FONT_SUBTITLE);
        lblRecentTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBtns.setBackground(UITheme.CARD_BG);

        JButton btnViewDetails = new JButton("View Details");
        UITheme.stylePrimaryButton(btnViewDetails);
        btnViewDetails.addActionListener(e -> viewSelectedPublicationDetails());

        JButton btnRefreshData = new JButton("Refresh");
        UITheme.styleSecondaryButton(btnRefreshData);
        btnRefreshData.addActionListener(e -> loadDashboardData());

        rightBtns.add(btnViewDetails);
        rightBtns.add(btnRefreshData);

        recentHeader.add(lblRecentTitle, BorderLayout.WEST);
        recentHeader.add(rightBtns, BorderLayout.EAST);
        recentPanel.add(recentHeader, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Author / Faculty", "Department", "Type", "Year", "Journal / Conference", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRecentPubs = new JTable(tableModel);
        UITheme.styleTable(tblRecentPubs);

        tblRecentPubs.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblRecentPubs.getColumnModel().getColumn(1).setPreferredWidth(280);
        tblRecentPubs.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblRecentPubs.getColumnModel().getColumn(3).setPreferredWidth(140);
        tblRecentPubs.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblRecentPubs.getColumnModel().getColumn(5).setPreferredWidth(55);
        tblRecentPubs.getColumnModel().getColumn(6).setPreferredWidth(200);
        tblRecentPubs.getColumnModel().getColumn(7).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tblRecentPubs);
        scrollPane.getViewport().setBackground(UITheme.TABLE_ROW_EVEN);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(recentPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 6));

        JPanel line = new JPanel();
        line.setBackground(accentColor);
        line.setPreferredSize(new Dimension(0, 4));
        card.add(line, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.CARD_BG);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_SECONDARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(UITheme.FONT_METRIC_VALUE);
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(lblTitle);
        content.add(Box.createRigidArea(new Dimension(0, 4)));
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UITheme.SURFACE_BG);
        menuBar.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));

        JMenu menuFile = new JMenu("File");
        menuFile.setFont(UITheme.FONT_BOLD);
        menuFile.setForeground(UITheme.TEXT_PRIMARY);

        JMenuItem miDashboard = new JMenuItem("Dashboard");
        miDashboard.addActionListener(e -> loadDashboardData());
        JMenuItem miLogout = new JMenuItem("Logout");
        miLogout.addActionListener(e -> performLogout());
        JMenuItem miExit = new JMenuItem("Exit");
        miExit.addActionListener(e -> System.exit(0));
        menuFile.add(miDashboard);
        menuFile.addSeparator();
        menuFile.add(miLogout);
        menuFile.add(miExit);

        JMenu menuPubs = new JMenu("Publications");
        menuPubs.setFont(UITheme.FONT_BOLD);
        menuPubs.setForeground(UITheme.TEXT_PRIMARY);
        JMenuItem miManagePubs = new JMenuItem("View Publications");
        miManagePubs.addActionListener(e -> openPublicationManagement());
        JMenuItem miSearchPubs = new JMenuItem("Search Publications");
        miSearchPubs.addActionListener(e -> openSearchPublications());
        menuPubs.add(miManagePubs);
        menuPubs.add(miSearchPubs);

        JMenu menuAccount = new JMenu("Account");
        menuAccount.setFont(UITheme.FONT_BOLD);
        menuAccount.setForeground(UITheme.TEXT_PRIMARY);
        JMenuItem miChangePass = new JMenuItem("Change Password");
        miChangePass.addActionListener(e -> openChangePassword());
        JMenuItem miAccLogout = new JMenuItem("Logout");
        miAccLogout.addActionListener(e -> performLogout());
        menuAccount.add(miChangePass);
        menuAccount.addSeparator();
        menuAccount.add(miAccLogout);

        JMenu menuHelp = new JMenu("Help");
        menuHelp.setFont(UITheme.FONT_BOLD);
        menuHelp.setForeground(UITheme.TEXT_PRIMARY);
        JMenuItem miAbout = new JMenuItem("About Project");
        miAbout.addActionListener(e -> showAboutDialog());
        menuHelp.add(miAbout);

        menuBar.add(menuFile);
        menuBar.add(menuPubs);
        menuBar.add(menuAccount);
        menuBar.add(menuHelp);

        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(UITheme.CARD_BG);
        toolBar.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton btnDash = new JButton("Dashboard");
        UITheme.styleSecondaryButton(btnDash);
        btnDash.addActionListener(e -> loadDashboardData());

        JButton btnView = new JButton("View Publications");
        UITheme.stylePrimaryButton(btnView);
        btnView.addActionListener(e -> openPublicationManagement());

        JButton btnSearch = new JButton("Search");
        UITheme.styleSecondaryButton(btnSearch);
        btnSearch.addActionListener(e -> openSearchPublications());

        JButton btnRefresh = new JButton("Refresh");
        UITheme.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadDashboardData());

        JButton btnLogout = new JButton("Logout");
        UITheme.styleDangerButton(btnLogout);
        btnLogout.addActionListener(e -> performLogout());

        toolBar.add(btnDash);
        toolBar.addSeparator(new Dimension(8, 0));
        toolBar.add(btnView);
        toolBar.add(Box.createRigidArea(new Dimension(6, 0)));
        toolBar.add(btnSearch);
        toolBar.addSeparator(new Dimension(8, 0));
        toolBar.add(btnRefresh);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(btnLogout);

        return toolBar;
    }

    public void loadDashboardData() {
        try {
            int facultyCount = facultyDAO.getTotalFacultyCount();
            int pubCount = publicationDAO.getTotalPublicationCount();
            int currentYear = Year.now().getValue();
            int yearPubCount = publicationDAO.getPublicationsCountByYear(currentYear);
            int deptCount = facultyDAO.getDistinctDepartmentCount();

            lblTotalFaculty.setText(String.valueOf(facultyCount));
            lblTotalPublications.setText(String.valueOf(pubCount));
            lblCurrentYearPubs.setText(String.valueOf(yearPubCount));
            lblTotalDepts.setText(String.valueOf(deptCount));

            tableModel.setRowCount(0);
            List<Publication> recents = publicationDAO.getRecentPublications(10);
            for (Publication p : recents) {
                tableModel.addRow(new Object[]{
                        p.getPublicationId(),
                        p.getTitle(),
                        p.getFacultyName(),
                        p.getDepartment(),
                        p.getPublicationType(),
                        p.getPublicationYear(),
                        p.getJournalConference(),
                        p.getPublicationStatus()
                });
            }
        } catch (SQLException e) {
            ValidationUtil.showError(this, "Failed to load dashboard metrics: " + e.getMessage());
        }
    }

    private void openPublicationManagement() {
        PublicationFrame frame = new PublicationFrame(this);
        frame.setVisible(true);
    }

    private void openSearchPublications() {
        SearchPublicationFrame frame = new SearchPublicationFrame(this);
        frame.setVisible(true);
    }

    private void openChangePassword() {
        ChangePasswordFrame dialog = new ChangePasswordFrame(this);
        dialog.setVisible(true);
    }

    private void viewSelectedPublicationDetails() {
        int selectedRow = tblRecentPubs.getSelectedRow();
        if (selectedRow < 0) {
            ValidationUtil.showWarning(this, "Please select a publication to view details.");
            return;
        }
        int publicationId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Publication p = publicationDAO.getPublicationById(publicationId);
            if (p != null) {
                PublicationDetailsFrame details = new PublicationDetailsFrame(this, p);
                details.setVisible(true);
            }
        } catch (SQLException ex) {
            ValidationUtil.showError(this, "Error fetching publication details: " + ex.getMessage());
        }
    }

    private void performLogout() {
        if (ValidationUtil.confirmAction(this, "Are you sure you want to log out?", "Logout Confirmation")) {
            SessionManager.getInstance().clearSession();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void showAboutDialog() {
        String info = "Faculty Research Publication Repository\n" +
                      "Version 1.0.0 (B.Tech Minor Project)\n\n" +
                      "Developed with Java Swing, JDBC, and MySQL Database.\n" +
                      "Designed for faculty profile & academic research record management.";
        JOptionPane.showMessageDialog(this, info, "About Project", JOptionPane.INFORMATION_MESSAGE);
    }
}
