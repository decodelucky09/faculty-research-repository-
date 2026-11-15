package com.facultyrepository.ui;

import com.facultyrepository.dao.FacultyDAO;
import com.facultyrepository.dao.PublicationDAO;
import com.facultyrepository.model.Faculty;
import com.facultyrepository.model.Publication;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.UITheme;
import com.facultyrepository.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Screen 4 — Add / Edit Publication Form (Dark Mode)
 */
public class AddPublicationFrame extends JFrame {

    private final PublicationDAO publicationDAO = new PublicationDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final JFrame parentFrame;
    private final Publication editingPublication;

    private JComboBox<FacultyItem> cmbFaculty;
    private JTextField txtTitle;
    private JComboBox<String> cmbType;
    private JTextField txtJournalConference;
    private JTextField txtResearchArea;
    private JTextField txtYear;
    private JTextField txtDoi;
    private JTextField txtIssnIsbn;
    private JTextField txtPublisher;
    private JComboBox<String> cmbStatus;
    private JTextField txtLink;
    private JTextArea txtAbstract;

    private JButton btnSave;
    private JButton btnClear;
    private JButton btnCancel;

    public static class FacultyItem {
        private final int id;
        private final String name;
        private final String department;

        public FacultyItem(int id, String name, String department) {
            this.id = id;
            this.name = name;
            this.department = department;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name + " (" + department + ")";
        }
    }

    public AddPublicationFrame(JFrame parent, Publication existing) {
        this.parentFrame = parent;
        this.editingPublication = existing;
        initComponents();
        AppIconUtil.applyWindowIcon(this);
        loadFacultyList();
        if (editingPublication != null) {
            populateFieldsForEdit();
        }
    }

    private void initComponents() {
        boolean isEdit = (editingPublication != null);
        setTitle((isEdit ? "Edit" : "Add New") + " Publication - Faculty Research Repository");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(840, 820);
        setLocationRelativeTo(parentFrame);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.CARD_BG);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTitle = new JLabel(isEdit ? "Edit Research Publication Record" : "Add New Research Publication Record");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Enter research publication metadata according to indexing standards.");
        lblSubtitle.setFont(UITheme.FONT_REGULAR);
        lblSubtitle.setForeground(UITheme.ACCENT_CYAN);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(UITheme.CARD_BG);
        headerText.add(lblTitle);
        headerText.add(Box.createRigidArea(new Dimension(0, 4)));
        headerText.add(lblSubtitle);

        header.add(headerText, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Center Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // 1. Faculty
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formCard.add(createFieldLabel("Faculty Author *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbFaculty = new JComboBox<>();
        UITheme.styleComboBox(cmbFaculty);
        formCard.add(cmbFaculty, gbc);

        // 2. Title
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Publication Title *:"), gbc);
        gbc.gridx = 1;
        txtTitle = new JTextField();
        UITheme.styleTextField(txtTitle);
        formCard.add(txtTitle, gbc);

        // 3. Type
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Publication Type *:"), gbc);
        gbc.gridx = 1;
        String[] types = {"Journal", "Conference", "Book Chapter", "Patent", "Workshop", "Preprint"};
        cmbType = new JComboBox<>(types);
        UITheme.styleComboBox(cmbType);
        formCard.add(cmbType, gbc);

        // 4. Venue
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Journal / Conference *:"), gbc);
        gbc.gridx = 1;
        txtJournalConference = new JTextField();
        UITheme.styleTextField(txtJournalConference);
        formCard.add(txtJournalConference, gbc);

        // 5. Research Area
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Research Area *:"), gbc);
        gbc.gridx = 1;
        txtResearchArea = new JTextField();
        UITheme.styleTextField(txtResearchArea);
        formCard.add(txtResearchArea, gbc);

        // 6. Year
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Publication Year *:"), gbc);
        gbc.gridx = 1;
        txtYear = new JTextField();
        UITheme.styleTextField(txtYear);
        txtYear.setText(String.valueOf(java.time.Year.now().getValue()));
        formCard.add(txtYear, gbc);

        // 7. DOI
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("DOI:"), gbc);
        gbc.gridx = 1;
        txtDoi = new JTextField();
        UITheme.styleTextField(txtDoi);
        formCard.add(txtDoi, gbc);

        // 8. ISSN/ISBN
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("ISSN / ISBN:"), gbc);
        gbc.gridx = 1;
        txtIssnIsbn = new JTextField();
        UITheme.styleTextField(txtIssnIsbn);
        formCard.add(txtIssnIsbn, gbc);

        // 9. Publisher
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        txtPublisher = new JTextField();
        UITheme.styleTextField(txtPublisher);
        formCard.add(txtPublisher, gbc);

        // 10. Status
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Publication Status *:"), gbc);
        gbc.gridx = 1;
        String[] statuses = {"Published", "Accepted", "Under Review", "Submitted"};
        cmbStatus = new JComboBox<>(statuses);
        UITheme.styleComboBox(cmbStatus);
        formCard.add(cmbStatus, gbc);

        // 11. Link
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(createFieldLabel("Publication Link:"), gbc);
        gbc.gridx = 1;
        txtLink = new JTextField();
        UITheme.styleTextField(txtLink);
        formCard.add(txtLink, gbc);

        // 12. Abstract
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formCard.add(createFieldLabel("Abstract / Summary:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtAbstract = new JTextArea(5, 25);
        txtAbstract.setFont(UITheme.FONT_REGULAR);
        txtAbstract.setLineWrap(true);
        txtAbstract.setWrapStyleWord(true);
        txtAbstract.setBackground(UITheme.SURFACE_BG);
        txtAbstract.setForeground(UITheme.TEXT_PRIMARY);
        txtAbstract.setCaretColor(UITheme.PRIMARY_COLOR);
        JScrollPane spAbstract = new JScrollPane(txtAbstract);
        spAbstract.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        formCard.add(spAbstract, gbc);

        JScrollPane mainScroll = new JScrollPane(formCard);
        mainScroll.getViewport().setBackground(UITheme.BG_DARK);
        mainScroll.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(mainScroll, BorderLayout.CENTER);

        // Bottom Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomBar.setBackground(UITheme.BG_DARK);

        btnSave = new JButton(isEdit ? "Update Publication" : "Save Publication");
        UITheme.styleSuccessButton(btnSave);
        btnSave.addActionListener(e -> savePublication());

        btnClear = new JButton("Clear");
        UITheme.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        btnCancel = new JButton("Cancel");
        UITheme.styleDangerButton(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        bottomBar.add(btnSave);
        if (!isEdit) {
            bottomBar.add(btnClear);
        }
        bottomBar.add(btnCancel);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    private void loadFacultyList() {
        cmbFaculty.removeAllItems();
        try {
            List<Faculty> faculties = facultyDAO.getAllFaculty();
            for (Faculty f : faculties) {
                cmbFaculty.addItem(new FacultyItem(f.getFacultyId(), f.getFacultyName(), f.getDepartment()));
            }
        } catch (SQLException e) {
            ValidationUtil.showError(this, "Failed to load faculty list: " + e.getMessage());
        }
    }

    private void populateFieldsForEdit() {
        txtTitle.setText(editingPublication.getTitle());
        cmbType.setSelectedItem(editingPublication.getPublicationType());
        txtJournalConference.setText(editingPublication.getJournalConference());
        txtResearchArea.setText(editingPublication.getResearchArea());
        txtYear.setText(String.valueOf(editingPublication.getPublicationYear()));
        txtDoi.setText(editingPublication.getDoi() != null ? editingPublication.getDoi() : "");
        txtIssnIsbn.setText(editingPublication.getIssnIsbn() != null ? editingPublication.getIssnIsbn() : "");
        txtPublisher.setText(editingPublication.getPublisher() != null ? editingPublication.getPublisher() : "");
        cmbStatus.setSelectedItem(editingPublication.getPublicationStatus());
        txtLink.setText(editingPublication.getPublicationLink() != null ? editingPublication.getPublicationLink() : "");
        txtAbstract.setText(editingPublication.getAbstractText() != null ? editingPublication.getAbstractText() : "");

        for (int i = 0; i < cmbFaculty.getItemCount(); i++) {
            FacultyItem item = cmbFaculty.getItemAt(i);
            if (item.getId() == editingPublication.getFacultyId()) {
                cmbFaculty.setSelectedIndex(i);
                break;
            }
        }
    }

    private void savePublication() {
        if (cmbFaculty.getSelectedItem() == null) {
            ValidationUtil.showWarning(this, "Please select an author/faculty member!");
            return;
        }
        if (!ValidationUtil.validateRequired(this, txtTitle.getText(), "Publication Title")) return;
        if (!ValidationUtil.validateRequired(this, txtJournalConference.getText(), "Journal / Conference")) return;
        if (!ValidationUtil.validateRequired(this, txtResearchArea.getText(), "Research Area")) return;
        if (!ValidationUtil.validateYear(this, txtYear.getText())) return;
        if (!ValidationUtil.validateDOI(this, txtDoi.getText())) return;

        FacultyItem selFaculty = (FacultyItem) cmbFaculty.getSelectedItem();
        int facultyId = selFaculty.getId();
        int year = Integer.parseInt(txtYear.getText().trim());

        Publication pub = (editingPublication != null) ? editingPublication : new Publication();
        pub.setFacultyId(facultyId);
        pub.setTitle(txtTitle.getText().trim());
        pub.setPublicationType(cmbType.getSelectedItem().toString());
        pub.setJournalConference(txtJournalConference.getText().trim());
        pub.setResearchArea(txtResearchArea.getText().trim());
        pub.setPublicationYear(year);
        pub.setDoi(txtDoi.getText().trim());
        pub.setIssnIsbn(txtIssnIsbn.getText().trim());
        pub.setPublisher(txtPublisher.getText().trim());
        pub.setPublicationStatus(cmbStatus.getSelectedItem().toString());
        pub.setPublicationLink(txtLink.getText().trim());
        pub.setAbstractText(txtAbstract.getText().trim());

        try {
            boolean success;
            if (editingPublication != null) {
                success = publicationDAO.updatePublication(pub);
            } else {
                success = publicationDAO.addPublication(pub);
            }

            if (success) {
                ValidationUtil.showSuccess(this, "Publication record " + (editingPublication != null ? "updated" : "saved") + " successfully!");
                dispose();

                if (parentFrame instanceof PublicationFrame) {
                    ((PublicationFrame) parentFrame).loadPublicationData(null);
                } else if (parentFrame instanceof AdminDashboard) {
                    ((AdminDashboard) parentFrame).loadDashboardData();
                }
            } else {
                ValidationUtil.showError(this, "Failed to save publication to database.");
            }
        } catch (SQLException ex) {
            ValidationUtil.showError(this, "Database Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        if (cmbFaculty.getItemCount() > 0) cmbFaculty.setSelectedIndex(0);
        txtTitle.setText("");
        cmbType.setSelectedIndex(0);
        txtJournalConference.setText("");
        txtResearchArea.setText("");
        txtYear.setText(String.valueOf(java.time.Year.now().getValue()));
        txtDoi.setText("");
        txtIssnIsbn.setText("");
        txtPublisher.setText("");
        cmbStatus.setSelectedIndex(0);
        txtLink.setText("");
        txtAbstract.setText("");
        txtTitle.requestFocus();
    }
}
