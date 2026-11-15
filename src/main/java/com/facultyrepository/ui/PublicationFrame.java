package com.facultyrepository.ui;

import com.facultyrepository.dao.FacultyDAO;
import com.facultyrepository.dao.PublicationDAO;
import com.facultyrepository.model.Publication;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.SessionManager;
import com.facultyrepository.util.UITheme;
import com.facultyrepository.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Screen 5 — Publication Management Screen (Dark Mode)
 */
public class PublicationFrame extends JFrame {

    private final PublicationDAO publicationDAO = new PublicationDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final JFrame parentDashboard;

    private JTable tblPublications;
    private DefaultTableModel tableModel;

    private JTextField txtSearchKeyword;
    private JComboBox<String> cmbTypeFilter;
    private JComboBox<String> cmbDeptFilter;
    private JComboBox<String> cmbStatusFilter;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnViewDetails;
    private JButton btnSearch;
    private JButton btnClear;
    private JButton btnRefresh;
    private JButton btnBack;

    public PublicationFrame(JFrame parent) {
        this.parentDashboard = parent;
        initComponents();
        AppIconUtil.applyWindowIcon(this);
        loadFilters();
        loadPublicationData(null);
    }

    private void initComponents() {
        setTitle("Research Publication Management - Faculty Research Repository");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1240, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.CARD_BG);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Research Publication Repository");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        btnBack = new JButton("← Back to Dashboard");
        UITheme.styleSecondaryButton(btnBack);
        btnBack.addActionListener(e -> {
            dispose();
            if (parentDashboard != null) {
                if (parentDashboard instanceof AdminDashboard) {
                    ((AdminDashboard) parentDashboard).loadDashboardData();
                } else if (parentDashboard instanceof UserDashboard) {
                    ((UserDashboard) parentDashboard).loadDashboardData();
                }
                parentDashboard.setVisible(true);
            }
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setBackground(UITheme.BG_DARK);
        centerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Search & Filter Bar Card
        JPanel filterCard = UITheme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        filterCard.add(createLabel("Keyword:"));
        txtSearchKeyword = new JTextField(12);
        UITheme.styleTextField(txtSearchKeyword);
        filterCard.add(txtSearchKeyword);

        filterCard.add(createLabel("Type:"));
        cmbTypeFilter = new JComboBox<>(new String[]{"All Types", "Journal", "Conference", "Book Chapter", "Patent", "Workshop", "Preprint"});
        UITheme.styleComboBox(cmbTypeFilter);
        filterCard.add(cmbTypeFilter);

        filterCard.add(createLabel("Department:"));
        cmbDeptFilter = new JComboBox<>(new String[]{"All Departments"});
        UITheme.styleComboBox(cmbDeptFilter);
        filterCard.add(cmbDeptFilter);

        filterCard.add(createLabel("Status:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"All Statuses", "Published", "Accepted", "Under Review", "Submitted"});
        UITheme.styleComboBox(cmbStatusFilter);
        filterCard.add(cmbStatusFilter);

        btnSearch = new JButton("Apply Filter");
        UITheme.stylePrimaryButton(btnSearch);
        btnSearch.addActionListener(e -> applyFilter());
        filterCard.add(btnSearch);

        btnClear = new JButton("Clear");
        UITheme.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearFilters());
        filterCard.add(btnClear);

        centerPanel.add(filterCard, BorderLayout.NORTH);

        // 2. Table Section
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 10));

        String[] columns = {
                "ID", "Title", "Faculty / Author", "Department", "Type",
                "Journal / Conference", "Research Area", "Year", "DOI", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPublications = new JTable(tableModel);
        UITheme.styleTable(tblPublications);

        tblPublications.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblPublications.getColumnModel().getColumn(1).setPreferredWidth(260);
        tblPublications.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblPublications.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblPublications.getColumnModel().getColumn(4).setPreferredWidth(95);
        tblPublications.getColumnModel().getColumn(5).setPreferredWidth(200);
        tblPublications.getColumnModel().getColumn(6).setPreferredWidth(130);
        tblPublications.getColumnModel().getColumn(7).setPreferredWidth(55);
        tblPublications.getColumnModel().getColumn(8).setPreferredWidth(140);
        tblPublications.getColumnModel().getColumn(9).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tblPublications);
        scrollPane.getViewport().setBackground(UITheme.TABLE_ROW_EVEN);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setBackground(UITheme.CARD_BG);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftActions.setBackground(UITheme.CARD_BG);

        btnViewDetails = new JButton("View Details");
        UITheme.stylePrimaryButton(btnViewDetails);
        btnViewDetails.addActionListener(e -> viewDetailsAction());
        leftActions.add(btnViewDetails);

        btnRefresh = new JButton("Refresh");
        UITheme.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> {
            clearFilters();
            loadPublicationData(null);
        });
        leftActions.add(btnRefresh);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setBackground(UITheme.CARD_BG);

        btnAdd = new JButton("+ Add Publication");
        UITheme.styleSuccessButton(btnAdd);
        btnAdd.addActionListener(e -> addPublicationAction());

        btnEdit = new JButton("Edit / Update");
        UITheme.stylePrimaryButton(btnEdit);
        btnEdit.addActionListener(e -> editPublicationAction());

        btnDelete = new JButton("Delete");
        UITheme.styleDangerButton(btnDelete);
        btnDelete.addActionListener(e -> deletePublicationAction());

        boolean isAdmin = SessionManager.getInstance().isAdmin();
        btnAdd.setEnabled(isAdmin);
        btnEdit.setEnabled(isAdmin);
        btnDelete.setEnabled(isAdmin);

        rightActions.add(btnAdd);
        rightActions.add(btnEdit);
        rightActions.add(btnDelete);

        actionBar.add(leftActions, BorderLayout.WEST);
        actionBar.add(rightActions, BorderLayout.EAST);
        tableCard.add(actionBar, BorderLayout.SOUTH);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    private void loadFilters() {
        try {
            List<String> depts = facultyDAO.getAllDepartments();
            for (String d : depts) {
                cmbDeptFilter.addItem(d);
            }
        } catch (SQLException e) {
            System.err.println("Could not load departments: " + e.getMessage());
        }
    }

    public void loadPublicationData(List<Publication> optionalList) {
        tableModel.setRowCount(0);
        try {
            List<Publication> list = (optionalList != null) ? optionalList : publicationDAO.getAllPublications();
            for (Publication p : list) {
                tableModel.addRow(new Object[]{
                        p.getPublicationId(),
                        p.getTitle(),
                        p.getFacultyName(),
                        p.getDepartment(),
                        p.getPublicationType(),
                        p.getJournalConference(),
                        p.getResearchArea(),
                        p.getPublicationYear(),
                        p.getDoi() != null ? p.getDoi() : "—",
                        p.getPublicationStatus()
                });
            }
        } catch (SQLException e) {
            ValidationUtil.showError(this, "Failed to retrieve publications: " + e.getMessage());
        }
    }

    private void applyFilter() {
        String keyword = txtSearchKeyword.getText().trim();
        String type = cmbTypeFilter.getSelectedItem().toString();
        String dept = cmbDeptFilter.getSelectedItem().toString();
        String status = cmbStatusFilter.getSelectedItem().toString();

        try {
            List<Publication> filtered = publicationDAO.searchAndFilter(
                    keyword, null, dept, null, null, type, null, status
            );
            loadPublicationData(filtered);
        } catch (SQLException ex) {
            ValidationUtil.showError(this, "Error during filter query: " + ex.getMessage());
        }
    }

    private void clearFilters() {
        txtSearchKeyword.setText("");
        cmbTypeFilter.setSelectedIndex(0);
        cmbDeptFilter.setSelectedIndex(0);
        cmbStatusFilter.setSelectedIndex(0);
        loadPublicationData(null);
    }

    private void viewDetailsAction() {
        int row = tblPublications.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showWarning(this, "Please select a publication from the table to view.");
            return;
        }
        int pubId = (int) tableModel.getValueAt(row, 0);
        try {
            Publication p = publicationDAO.getPublicationById(pubId);
            if (p != null) {
                PublicationDetailsFrame details = new PublicationDetailsFrame(this, p);
                details.setVisible(true);
            }
        } catch (SQLException e) {
            ValidationUtil.showError(this, "Failed to load publication details: " + e.getMessage());
        }
    }

    private void addPublicationAction() {
        AddPublicationFrame frame = new AddPublicationFrame(this, null);
        frame.setVisible(true);
    }

    private void editPublicationAction() {
        int row = tblPublications.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showWarning(this, "Please select a publication from the table to edit.");
            return;
        }
        int pubId = (int) tableModel.getValueAt(row, 0);
        try {
            Publication p = publicationDAO.getPublicationById(pubId);
            if (p != null) {
                AddPublicationFrame frame = new AddPublicationFrame(this, p);
                frame.setVisible(true);
            }
        } catch (SQLException e) {
            ValidationUtil.showError(this, "Failed to load publication for edit: " + e.getMessage());
        }
    }

    private void deletePublicationAction() {
        int row = tblPublications.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showWarning(this, "Please select a publication from the table to delete.");
            return;
        }
        int pubId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        boolean confirm = ValidationUtil.confirmAction(this,
                "Are you sure you want to delete publication:\n\"" + title + "\"?",
                "Confirm Delete Publication");

        if (confirm) {
            try {
                if (publicationDAO.deletePublication(pubId)) {
                    ValidationUtil.showSuccess(this, "Publication record deleted successfully.");
                    loadPublicationData(null);
                } else {
                    ValidationUtil.showError(this, "Failed to delete publication record.");
                }
            } catch (SQLException e) {
                ValidationUtil.showError(this, "Database Error: " + e.getMessage());
            }
        }
    }
}
