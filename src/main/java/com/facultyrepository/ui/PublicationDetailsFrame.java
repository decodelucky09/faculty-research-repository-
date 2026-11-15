package com.facultyrepository.ui;

import com.facultyrepository.model.Publication;
import com.facultyrepository.util.AppIconUtil;
import com.facultyrepository.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URI;

/**
 * Screen 6 — Publication Details Frame (Dark Mode)
 */
public class PublicationDetailsFrame extends JDialog {

    public PublicationDetailsFrame(JFrame parent, Publication p) {
        super(parent, "Publication Details - " + p.getTitle(), true);
        initComponents(p);
        AppIconUtil.applyWindowIcon(this);
    }

    private void initComponents(Publication p) {
        setSize(860, 720);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.CARD_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Publication Details");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Publication #" + p.getPublicationId() + "  •  " + p.getPublicationType() + "  •  Status: " + p.getPublicationStatus());
        lblSubtitle.setFont(UITheme.FONT_REGULAR);
        lblSubtitle.setForeground(UITheme.ACCENT_CYAN);

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(UITheme.CARD_BG);
        headerContent.add(lblTitle);
        headerContent.add(Box.createRigidArea(new Dimension(0, 4)));
        headerContent.add(lblSubtitle);

        header.add(headerContent, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Center Content Card
        JPanel bodyCard = UITheme.createCardPanel();
        bodyCard.setLayout(new BorderLayout(0, 14));

        // Details Grid
        JPanel metaGrid = new JPanel(new GridLayout(0, 2, 14, 12));
        metaGrid.setBackground(UITheme.CARD_BG);

        addDetailItem(metaGrid, "Title:", p.getTitle());
        addDetailItem(metaGrid, "Faculty / Author:", p.getFacultyName() + " (" + p.getDepartment() + ")");
        addDetailItem(metaGrid, "Publication Type:", p.getPublicationType());
        addDetailItem(metaGrid, "Publication Status:", p.getPublicationStatus());
        addDetailItem(metaGrid, "Journal / Conference:", p.getJournalConference());
        addDetailItem(metaGrid, "Research Area:", p.getResearchArea());
        addDetailItem(metaGrid, "Publication Year:", String.valueOf(p.getPublicationYear()));
        addDetailItem(metaGrid, "Publisher:", p.getPublisher() != null ? p.getPublisher() : "N/A");
        addDetailItem(metaGrid, "DOI:", p.getDoi() != null && !p.getDoi().isEmpty() ? p.getDoi() : "N/A");
        addDetailItem(metaGrid, "ISSN / ISBN:", p.getIssnIsbn() != null && !p.getIssnIsbn().isEmpty() ? p.getIssnIsbn() : "N/A");
        addDetailItem(metaGrid, "Publication Link:", p.getPublicationLink() != null && !p.getPublicationLink().isEmpty() ? p.getPublicationLink() : "N/A");
        addDetailItem(metaGrid, "Date Added:", p.getDateAdded() != null ? p.getDateAdded().toString() : "N/A");

        bodyCard.add(metaGrid, BorderLayout.NORTH);

        // Abstract Section
        JPanel abstractPanel = new JPanel(new BorderLayout(0, 6));
        abstractPanel.setBackground(UITheme.CARD_BG);

        JLabel lblAbsHeader = new JLabel("Abstract / Research Summary:");
        lblAbsHeader.setFont(UITheme.FONT_BOLD);
        lblAbsHeader.setForeground(UITheme.ACCENT_CYAN);
        abstractPanel.add(lblAbsHeader, BorderLayout.NORTH);

        JTextArea txtAbstract = new JTextArea();
        txtAbstract.setFont(UITheme.FONT_REGULAR);
        txtAbstract.setLineWrap(true);
        txtAbstract.setWrapStyleWord(true);
        txtAbstract.setEditable(false);
        txtAbstract.setBackground(UITheme.SURFACE_BG);
        txtAbstract.setForeground(UITheme.TEXT_PRIMARY);
        txtAbstract.setText(p.getAbstractText() != null && !p.getAbstractText().isEmpty()
                ? p.getAbstractText()
                : "No abstract provided for this publication record.");

        JScrollPane sp = new JScrollPane(txtAbstract);
        sp.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        sp.setPreferredSize(new Dimension(0, 160));
        abstractPanel.add(sp, BorderLayout.CENTER);

        bodyCard.add(abstractPanel, BorderLayout.CENTER);

        JScrollPane mainScroll = new JScrollPane(bodyCard);
        mainScroll.getViewport().setBackground(UITheme.BG_DARK);
        mainScroll.setBorder(new EmptyBorder(16, 20, 16, 20));
        add(mainScroll, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottomBar.setBackground(UITheme.BG_DARK);

        if (p.getPublicationLink() != null && p.getPublicationLink().startsWith("http")) {
            JButton btnOpenLink = new JButton("Open URL in Browser");
            UITheme.stylePrimaryButton(btnOpenLink);
            btnOpenLink.addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(p.getPublicationLink()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not open browser: " + ex.getMessage());
                }
            });
            bottomBar.add(btnOpenLink);
        }

        JButton btnClose = new JButton("Close");
        UITheme.styleSecondaryButton(btnClose);
        btnClose.addActionListener(e -> dispose());
        bottomBar.add(btnClose);

        add(bottomBar, BorderLayout.SOUTH);
    }

    private void addDetailItem(JPanel parent, String label, String value) {
        JPanel item = new JPanel(new BorderLayout(4, 2));
        item.setBackground(UITheme.CARD_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_REGULAR);
        val.setForeground(UITheme.TEXT_PRIMARY);

        item.add(lbl, BorderLayout.NORTH);
        item.add(val, BorderLayout.CENTER);
        parent.add(item);
    }
}
