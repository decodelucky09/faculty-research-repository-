package com.facultyrepository.ui;

import com.facultyrepository.dao.FacultyDAO;
import com.facultyrepository.model.Faculty;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.SessionManager;
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
 * Screen 3 — Faculty Management Module (Dark Mode)
 */
public class FacultyFrame extends JFrame {

    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final JFrame parentDashboard;

    private JTextField txtFacultyId;
    private JTextField txtFacultyName;
    private JTextField txtEmployeeId;
    private JComboBox<String> cmbDepartment;
    private JComboBox<String> cmbDesignation;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtSpecialization;
    private JTextField txtSearch;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnBack;

    private JTable tblFaculty;
    private DefaultTableModel tableModel;

    public FacultyFrame(JFrame parent) {
        this.parentDashboard = parent;
        initComponents();
        AppIconUtil.applyWindowIcon(this);
        loadFacultyTable(null);
    }

    private void initComponents() {
        setTitle("Faculty Profile Management - Faculty Research Repository");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.CARD_BG);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Faculty Profile Management");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        btnBack = new JButton("← Back to Dashboard");
        UITheme.styleSecondaryButton(btnBack);
        btnBack.addActionListener(e -> {
            dispose();
            if (parentDashboard != null) {
                if (parentDashboard instanceof AdminDashboard) {
                    ((AdminDashboard) parentDashboard).loadDashboardData();
                }
                parentDashboard.setVisible(true);
            }
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel contentPanel = new JPanel(new BorderLayout(16, 0));
        contentPanel.setBackground(UITheme.BG_DARK);
        contentPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // LEFT: Form Panel
        JPanel leftCard = UITheme.createCardPanel();
        leftCard.setLayout(new BorderLayout(0, 10));
        leftCard.setPreferredSize(new Dimension(390, 0));

        TitledBorder formBorder = BorderFactory.createTitledBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1),
                "Faculty Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UITheme.FONT_SUBTITLE,
                UITheme.ACCENT_CYAN
        );
        leftCard.setBorder(BorderFactory.createCompoundBorder(formBorder, new EmptyBorder(10, 10, 10, 10)));

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setBackground(UITheme.CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Faculty ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formFields.add(createFormLabel("Faculty ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtFacultyId = new JTextField();
        UITheme.styleTextField(txtFacultyId);
        txtFacultyId.setEditable(false);
        txtFacultyId.setBackground(UITheme.BG_DARK);
        formFields.add(txtFacultyId, gbc);

        // 2. Employee ID
        gbc.gridx = 0; gbc.gridy = 1;
        formFields.add(createFormLabel("Employee ID *:"), gbc);
        gbc.gridx = 1;
        txtEmployeeId = new JTextField();
        UITheme.styleTextField(txtEmployeeId);
        formFields.add(txtEmployeeId, gbc);

        // 3. Name
        gbc.gridx = 0; gbc.gridy = 2;
        formFields.add(createFormLabel("Full Name *:"), gbc);
        gbc.gridx = 1;
        txtFacultyName = new JTextField();
        UITheme.styleTextField(txtFacultyName);
        formFields.add(txtFacultyName, gbc);

        // 4. Department
        gbc.gridx = 0; gbc.gridy = 3;
        formFields.add(createFormLabel("Department *:"), gbc);
        gbc.gridx = 1;
        String[] departments = {
                "Computer Science & Engineering",
                "Information Technology",
                "Electronics & Communication",
                "Mechanical Engineering",
                "Electrical Engineering",
                "Civil Engineering",
                "Applied Sciences"
        };
        cmbDepartment = new JComboBox<>(departments);
        cmbDepartment.setEditable(true);
        UITheme.styleComboBox(cmbDepartment);
        formFields.add(cmbDepartment, gbc);

        // 5. Designation
        gbc.gridx = 0; gbc.gridy = 4;
        formFields.add(createFormLabel("Designation *:"), gbc);
        gbc.gridx = 1;
        String[] designations = {
                "Professor & HOD",
                "Professor",
                "Associate Professor",
                "Assistant Professor",
                "Lecturer",
                "Adjunct Faculty",
                "Research Fellow"
        };
        cmbDesignation = new JComboBox<>(designations);
        cmbDesignation.setEditable(true);
        UITheme.styleComboBox(cmbDesignation);
        formFields.add(cmbDesignation, gbc);

        // 6. Email
        gbc.gridx = 0; gbc.gridy = 5;
        formFields.add(createFormLabel("Email *:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField();
        UITheme.styleTextField(txtEmail);
        formFields.add(txtEmail, gbc);

        // 7. Phone
        gbc.gridx = 0; gbc.gridy = 6;
        formFields.add(createFormLabel("Phone:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField();
        UITheme.styleTextField(txtPhone);
        formFields.add(txtPhone, gbc);

        // 8. Specialization
        gbc.gridx = 0; gbc.gridy = 7;
        formFields.add(createFormLabel("Specialization:"), gbc);
        gbc.gridx = 1;
        txtSpecialization = new JTextField();
        UITheme.styleTextField(txtSpecialization);
        formFields.add(txtSpecialization, gbc);

        leftCard.add(formFields, BorderLayout.CENTER);

        // Action Buttons Grid
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setBackground(UITheme.CARD_BG);
        btnGrid.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAdd = new JButton("Add Faculty");
        UITheme.styleSuccessButton(btnAdd);
        btnAdd.addActionListener(e -> addFacultyRecord());

        btnUpdate = new JButton("Update");
        UITheme.stylePrimaryButton(btnUpdate);
        btnUpdate.addActionListener(e -> updateFacultyRecord());

        btnDelete = new JButton("Delete");
        UITheme.styleDangerButton(btnDelete);
        btnDelete.addActionListener(e -> deleteFacultyRecord());

        btnClear = new JButton("Clear Form");
        UITheme.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        boolean isAdmin = SessionManager.getInstance().isAdmin();
        btnAdd.setEnabled(isAdmin);
        btnUpdate.setEnabled(isAdmin);
        btnDelete.setEnabled(isAdmin);

        btnGrid.add(btnAdd);
        btnGrid.add(btnUpdate);
        btnGrid.add(btnDelete);
        btnGrid.add(btnClear);

        leftCard.add(btnGrid, BorderLayout.SOUTH);
        contentPanel.add(leftCard, BorderLayout.WEST);

        // RIGHT: Table & Search Panel
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BorderLayout(0, 10));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(UITheme.CARD_BG);

        JLabel lblSearch = new JLabel("Search Faculty:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.TEXT_SECONDARY);
        searchBar.add(lblSearch, BorderLayout.WEST);

        txtSearch = new JTextField();
        UITheme.styleTextField(txtSearch);
        searchBar.add(txtSearch, BorderLayout.CENTER);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchActions.setBackground(UITheme.CARD_BG);

        btnSearch = new JButton("Search");
        UITheme.stylePrimaryButton(btnSearch);
        btnSearch.addActionListener(e -> searchFacultyRecords());

        btnRefresh = new JButton("Show All");
        UITheme.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadFacultyTable(null);
        });

        searchActions.add(btnSearch);
        searchActions.add(btnRefresh);
        searchBar.add(searchActions, BorderLayout.EAST);

        rightCard.add(searchBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Emp ID", "Name", "Department", "Designation", "Email", "Phone", "Specialization"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblFaculty = new JTable(tableModel);
        UITheme.styleTable(tblFaculty);
        tblFaculty.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblFaculty.getColumnModel().getColumn(1).setPreferredWidth(85);
        tblFaculty.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblFaculty.getColumnModel().getColumn(3).setPreferredWidth(160);
        tblFaculty.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblFaculty.getColumnModel().getColumn(5).setPreferredWidth(160);

        tblFaculty.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblFaculty.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromRow(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblFaculty);
        scrollPane.getViewport().setBackground(UITheme.TABLE_ROW_EVEN);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        rightCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(rightCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    private void loadFacultyTable(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Faculty> list;
            if (keyword != null && !keyword.trim().isEmpty()) {
                list = facultyDAO.searchFaculty(keyword);
            } else {
                list = facultyDAO.getAllFaculty();
            }
            for (Faculty f : list) {
                tableModel.addRow(new Object[]{
                        f.getFacultyId(),
                        f.getEmployeeId(),
                        f.getFacultyName(),
                        f.getDepartment(),
                        f.getDesignation(),
                        f.getEmail(),
                        f.getPhone(),
                        f.getSpecialization()
                });
            }
        } catch (SQLException e) {
            ValidationUtil.showError(this, "Failed to load faculty records: " + e.getMessage());
        }
    }

    private void populateFormFromRow(int row) {
        txtFacultyId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtEmployeeId.setText((String) tableModel.getValueAt(row, 1));
        txtFacultyName.setText((String) tableModel.getValueAt(row, 2));
        cmbDepartment.setSelectedItem(tableModel.getValueAt(row, 3));
        cmbDesignation.setSelectedItem(tableModel.getValueAt(row, 4));
        txtEmail.setText((String) tableModel.getValueAt(row, 5));
        Object phoneObj = tableModel.getValueAt(row, 6);
        txtPhone.setText(phoneObj != null ? phoneObj.toString() : "");
        Object specObj = tableModel.getValueAt(row, 7);
        txtSpecialization.setText(specObj != null ? specObj.toString() : "");
    }

    private void addFacultyRecord() {
        if (!validateInputs()) return;

        String empId = txtEmployeeId.getText().trim();
        try {
            if (facultyDAO.isEmployeeIdTaken(empId, -1)) {
                ValidationUtil.showWarning(this, "Employee ID '" + empId + "' is already registered! Please use a unique Employee ID.");
                txtEmployeeId.requestFocus();
                return;
            }

            Faculty f = new Faculty(
                    empId,
                    txtFacultyName.getText().trim(),
                    cmbDepartment.getSelectedItem().toString().trim(),
                    cmbDesignation.getSelectedItem().toString().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtSpecialization.getText().trim()
            );

            if (facultyDAO.addFaculty(f)) {
                ValidationUtil.showSuccess(this, "Faculty profile added successfully!");
                clearForm();
                loadFacultyTable(null);
            } else {
                ValidationUtil.showError(this, "Failed to insert faculty record.");
            }
        } catch (SQLException ex) {
            ValidationUtil.showError(this, "Database Error: " + ex.getMessage());
        }
    }

    private void updateFacultyRecord() {
        if (txtFacultyId.getText().trim().isEmpty()) {
            ValidationUtil.showWarning(this, "Please select a faculty record from the table to update.");
            return;
        }
        if (!validateInputs()) return;

        int facultyId = Integer.parseInt(txtFacultyId.getText().trim());
        String empId = txtEmployeeId.getText().trim();

        try {
            if (facultyDAO.isEmployeeIdTaken(empId, facultyId)) {
                ValidationUtil.showWarning(this, "Employee ID '" + empId + "' belongs to another faculty member!");
                txtEmployeeId.requestFocus();
                return;
            }

            Faculty f = new Faculty(
                    facultyId,
                    empId,
                    txtFacultyName.getText().trim(),
                    cmbDepartment.getSelectedItem().toString().trim(),
                    cmbDesignation.getSelectedItem().toString().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtSpecialization.getText().trim()
            );

            if (facultyDAO.updateFaculty(f)) {
                ValidationUtil.showSuccess(this, "Faculty record updated successfully!");
                clearForm();
                loadFacultyTable(null);
            } else {
                ValidationUtil.showError(this, "Failed to update record.");
            }
        } catch (SQLException ex) {
            ValidationUtil.showError(this, "Database Error: " + ex.getMessage());
        }
    }

    private void deleteFacultyRecord() {
        if (txtFacultyId.getText().trim().isEmpty()) {
            ValidationUtil.showWarning(this, "Please select a faculty record from the table to delete.");
            return;
        }

        int facultyId = Integer.parseInt(txtFacultyId.getText().trim());
        String facultyName = txtFacultyName.getText().trim();

        boolean confirm = ValidationUtil.confirmAction(this,
                "Are you sure you want to delete faculty '" + facultyName + "'?\n\n" +
                "WARNING: All research publications associated with this faculty will also be removed!",
                "Confirm Faculty Deletion");

        if (confirm) {
            try {
                if (facultyDAO.deleteFaculty(facultyId)) {
                    ValidationUtil.showSuccess(this, "Faculty profile deleted successfully.");
                    clearForm();
                    loadFacultyTable(null);
                } else {
                    ValidationUtil.showError(this, "Could not delete faculty record.");
                }
            } catch (SQLException ex) {
                ValidationUtil.showError(this, "Database Error: " + ex.getMessage());
            }
        }
    }

    private void searchFacultyRecords() {
        String query = txtSearch.getText().trim();
        loadFacultyTable(query);
    }

    private boolean validateInputs() {
        if (!ValidationUtil.validateRequired(this, txtEmployeeId.getText(), "Employee ID")) return false;
        if (!ValidationUtil.validateRequired(this, txtFacultyName.getText(), "Faculty Name")) return false;
        if (cmbDepartment.getSelectedItem() == null || cmbDepartment.getSelectedItem().toString().trim().isEmpty()) {
            ValidationUtil.showWarning(this, "Department cannot be empty!");
            return false;
        }
        if (cmbDesignation.getSelectedItem() == null || cmbDesignation.getSelectedItem().toString().trim().isEmpty()) {
            ValidationUtil.showWarning(this, "Designation cannot be empty!");
            return false;
        }
        if (!ValidationUtil.validateEmail(this, txtEmail.getText())) return false;
        if (!ValidationUtil.validatePhone(this, txtPhone.getText())) return false;
        return true;
    }

    private void clearForm() {
        txtFacultyId.setText("");
        txtEmployeeId.setText("");
        txtFacultyName.setText("");
        cmbDepartment.setSelectedIndex(0);
        cmbDesignation.setSelectedIndex(0);
        txtEmail.setText("");
        txtPhone.setText("");
        txtSpecialization.setText("");
        tblFaculty.clearSelection();
        txtEmployeeId.requestFocus();
    }
}
