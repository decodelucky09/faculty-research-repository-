package com.facultyrepository.ui;

import com.facultyrepository.dao.FacultyDAO;
import com.facultyrepository.dao.PublicationDAO;
import com.facultyrepository.model.Publication;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.UITheme;
import com.facultyrepository.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Screen 7 — Dedicated Search & Filter Laboratory (Dark Mode)
 */
public class SearchPublicationFrame extends JFrame {

    private final PublicationDAO publicationDAO = new PublicationDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final JFrame parentDashboard;

    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JComboBox<String> cmbDepartment;
    private JComboBox<String> cmbType;
    private JTextField txtJournal;
    private JComboBox<String> cmbResearchArea;
    private JTextField txtYear;
    private JComboBox<String> cmbStatus;

    private JButton btnSearch;
    private JButton btnReset;
    private JButton btnViewDetails;
    private JButton btnBack;

    private JTable tblResults;
    private DefaultTableModel tableModel;
    private JLabel lblResultCount;

    public SearchPublicationFrame(JFrame parent) {
        this.parentDashboard = parent;
        initComponents();
        AppIconUtil.applyWindowIcon(this);
        populateFilterDropdowns();
        executeSearch();
    }

    private void initComponents() {
        setTitle("Advanced Search & Analytics - Faculty Research Repository");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1240, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.CARD_BG);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Advanced Publication Search & Multi-Filter");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        btnBack = new JButton("← Back to Dashboard");
        UITheme.styleSecondaryButton(btnBack);
        btnBack.addActionListener(e -> {
            dispose();
            if (parentDashboard != null) {
                parentDashboard.setVisible(true);
            }
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setBackground(UITheme.BG_DARK);
        centerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Filter Criteria Card
        JPanel criteriaCard = UITheme.createCardPanel();
        criteriaCard.setLayout(new BorderLayout(0, 10));

        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1),
                "Multi-Attribute Filter Criteria",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UITheme.FONT_SUBTITLE,
                UITheme.ACCENT_CYAN
        );
        criteriaCard.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 14, 10, 14)));

        JPanel grid = new JPanel(new GridLayout(2, 4, 12, 8));
        grid.setBackground(UITheme.CARD_BG);

        // Field 1: Title
        grid.add(createFieldPanel("Title Keyword:", txtTitle = new JTextField()));
        // Field 2: Author
        grid.add(createFieldPanel("Author / Faculty Name:", txtAuthor = new JTextField()));
        // Field 3: Department
        cmbDepartment = new JComboBox<>(new String[]{"All Departments"});
        UITheme.styleComboBox(cmbDepartment);
        grid.add(createComboPanel("Department:", cmbDepartment));
        // Field 4: Publication Type
        cmbType = new JComboBox<>(new String[]{"All Types", "Journal", "Conference", "Book Chapter", "Patent", "Workshop", "Preprint"});
        UITheme.styleComboBox(cmbType);
        grid.add(createComboPanel("Publication Type:", cmbType));

        // Field 5: Journal / Conference
        grid.add(createFieldPanel("Journal / Conference:", txtJournal = new JTextField()));
        // Field 6: Research Area
        cmbResearchArea = new JComboBox<>(new String[]{"All Areas"});
        UITheme.styleComboBox(cmbResearchArea);
        grid.add(createComboPanel("Research Area:", cmbResearchArea));
        // Field 7: Year
        grid.add(createFieldPanel("Publication Year:", txtYear = new JTextField()));
        // Field 8: Status
        cmbStatus = new JComboBox<>(new String[]{"All Statuses", "Published", "Accepted", "Under Review", "Submitted"});
        UITheme.styleComboBox(cmbStatus);
        grid.add(createComboPanel("Publication Status:", cmbStatus));

        criteriaCard.add(grid, BorderLayout.CENTER);

        // Buttons Bar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnBar.setBackground(UITheme.CARD_BG);

        btnSearch = new JButton("Search Records");
        UITheme.stylePrimaryButton(btnSearch);
        btnSearch.addActionListener(e -> executeSearch());

        btnReset = new JButton("Reset Filters");
        UITheme.styleSecondaryButton(btnReset);
        btnReset.addActionListener(e -> resetFilters());

        btnBar.add(btnSearch);
        btnBar.add(btnReset);
        criteriaCard.add(btnBar, BorderLayout.SOUTH);

        centerPanel.add(criteriaCard, BorderLayout.NORTH);

        // 2. Results Table Card
        JPanel resultsCard = UITheme.createCardPanel();
        resultsCard.setLayout(new BorderLayout(0, 10));

        JPanel resultsHeader = new JPanel(new BorderLayout());
        resultsHeader.setBackground(UITheme.CARD_BG);

        lblResultCount = new JLabel("Found: 0 matching publication(s)");
        lblResultCount.setFont(UITheme.FONT_SUBTITLE);
        lblResultCount.setForeground(UITheme.ACCENT_CYAN);

        btnViewDetails = new JButton("View Selected Publication Details");
        UITheme.stylePrimaryButton(btnViewDetails);
        btnViewDetails.addActionListener(e -> viewDetails());

        resultsHeader.add(lblResultCount, BorderLayout.WEST);
        resultsHeader.add(btnViewDetails, BorderLayout.EAST);
        resultsCard.add(resultsHeader, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Author", "Department", "Type", "Journal / Conference", "Area", "Year", "DOI", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblResults = new JTable(tableModel);
        UITheme.styleTable(tblResults);

        tblResults.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblResults.getColumnModel().getColumn(1).setPreferredWidth(260);
        tblResults.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblResults.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblResults.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblResults.getColumnModel().getColumn(5).setPreferredWidth(200);
        tblResults.getColumnModel().getColumn(6).setPreferredWidth(130);
        tblResults.getColumnModel().getColumn(7).setPreferredWidth(55);
        tblResults.getColumnModel().getColumn(8).setPreferredWidth(140);
        tblResults.getColumnModel().getColumn(9).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(tblResults);
        scroll.getViewport().setBackground(UITheme.TABLE_ROW_EVEN);
        scroll.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        resultsCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(resultsCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setBackground(UITheme.CARD_BG);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_SECONDARY);
        UITheme.styleTextField(field);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel createComboPanel(String label, JComboBox<?> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setBackground(UITheme.CARD_BG);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_SECONDARY);
        p.add(l, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    private void populateFilterDropdowns() {
        try {
            List<String> depts = facultyDAO.getAllDepartments();
            for (String d : depts) cmbDepartment.addItem(d);

            List<String> areas = publicationDAO.getDistinctResearchAreas();
            for (String a : areas) cmbResearchArea.addItem(a);
        } catch (SQLException e) {
            System.err.println("Could not load dropdown filters: " + e.getMessage());
        }
    }

    private void executeSearch() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String dept = cmbDepartment.getSelectedItem() != null ? cmbDepartment.getSelectedItem().toString() : null;
        String type = cmbType.getSelectedItem() != null ? cmbType.getSelectedItem().toString() : null;
        String journal = txtJournal.getText().trim();
        String area = cmbResearchArea.getSelectedItem() != null ? cmbResearchArea.getSelectedItem().toString() : null;
        String status = cmbStatus.getSelectedItem() != null ? cmbStatus.getSelectedItem().toString() : null;

        Integer year = null;
        String yearText = txtYear.getText().trim();
        if (!yearText.isEmpty()) {
            try {
                year = Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                ValidationUtil.showWarning(this, "Year must be a valid 4-digit integer (e.g. 2026)!");
                return;
            }
        }

        try {
            List<Publication> results = publicationDAO.searchAndFilter(
                    title, author, dept, area, year, type, journal, status
            );

            tableModel.setRowCount(0);
            for (Publication p : results) {
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
            lblResultCount.setText("Found: " + results.size() + " matching publication(s)");
        } catch (SQLException e) {
            ValidationUtil.showError(this, "Search failed: " + e.getMessage());
        }
    }

    private void resetFilters() {
        txtTitle.setText("");
        txtAuthor.setText("");
        cmbDepartment.setSelectedIndex(0);
        cmbType.setSelectedIndex(0);
        txtJournal.setText("");
        cmbResearchArea.setSelectedIndex(0);
        txtYear.setText("");
        cmbStatus.setSelectedIndex(0);
        executeSearch();
    }

    private void viewDetails() {
        int row = tblResults.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showWarning(this, "Please select a publication from the search results to view.");
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
            ValidationUtil.showError(this, "Error: " + e.getMessage());
        }
    }
}
