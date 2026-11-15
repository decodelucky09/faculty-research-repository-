import os
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Image as RLImage, Table, TableStyle, PageBreak, KeepTogether
)
from reportlab.pdfgen import canvas
from PIL import Image as PILImage

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        if self._pageNumber == 1:
            # Skip header and footer on Cover Page
            return

        self.saveState()
        page_w, page_h = A4

        # Running Header
        self.setFont("Helvetica-Bold", 8)
        self.setFillColor(colors.HexColor("#1E3A8A"))
        self.drawString(36, page_h - 26, "FACULTY RESEARCH PUBLICATION REPOSITORY")

        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        self.drawRightString(page_w - 36, page_h - 26, "Chapter 5: Project Snapshots")

        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.75)
        self.line(36, page_h - 30, page_w - 36, page_h - 30)

        # Running Footer
        self.line(36, 30, page_w - 36, 30)
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        self.drawString(36, 20, "Student: Lucky Saini (PIET35CR030) | Guide: Ms. Siddhi Kumari Sharma")
        self.drawRightString(page_w - 36, 20, f"Page {self._pageNumber} of {page_count}")

        self.restoreState()


def build_pdf(output_pdf_path):
    doc = SimpleDocTemplate(
        output_pdf_path,
        pagesize=A4,
        leftMargin=36,
        rightMargin=36,
        topMargin=38,
        bottomMargin=38
    )

    page_w, page_h = A4
    printable_w = page_w - 72  # 523.27 pt
    printable_h = page_h - 80  # for content calculation

    styles = getSampleStyleSheet()

    # Custom Styles
    style_cover_title = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=28,
        alignment=1, # Center
        textColor=colors.HexColor("#1E3A8A"),
        spaceAfter=8
    )

    style_cover_subtitle = ParagraphStyle(
        'CoverSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=14,
        leading=18,
        alignment=1,
        textColor=colors.HexColor("#0D9488"),
        spaceAfter=15
    )

    style_cover_heading = ParagraphStyle(
        'CoverHeading',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=11,
        leading=15,
        alignment=1,
        textColor=colors.HexColor("#475569"),
        spaceAfter=25
    )

    style_fig_title = ParagraphStyle(
        'FigTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=16,
        textColor=colors.HexColor("#1E3A8A"),
        spaceAfter=4
    )

    style_fig_desc = ParagraphStyle(
        'FigDesc',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=colors.HexColor("#334155"),
        spaceAfter=8
    )

    style_fig_caption = ParagraphStyle(
        'FigCaption',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=14,
        alignment=1, # Center
        textColor=colors.HexColor("#1E293B"),
        spaceBefore=6,
        spaceAfter=4
    )

    elements = []

    # ==========================================
    # 1. COVER PAGE
    # ==========================================
    elements.append(Spacer(1, 25))

    logo_path = "src/main/resources/logo.jpg"
    if os.path.exists(logo_path):
        elements.append(RLImage(logo_path, width=130, height=130))
    elements.append(Spacer(1, 15))

    elements.append(Paragraph("FACULTY RESEARCH PUBLICATION REPOSITORY", style_cover_title))
    elements.append(Paragraph("A Desktop-Based Academic Publication & Profile Management System", style_cover_subtitle))
    elements.append(Paragraph("Developed using Java, Java Swing, JDBC & MySQL Database Engine", style_cover_heading))

    elements.append(Spacer(1, 15))

    # Student & Guide Information Table
    student_info = [
        [Paragraph("<b>Project Report Component:</b>", styles['Normal']), Paragraph("Chapter 5 — Snapshots of Project", styles['Normal'])],
        [Paragraph("<b>Project Title:</b>", styles['Normal']), Paragraph("Faculty Research Publication Repository", styles['Normal'])],
        [Paragraph("<b>Technology Stack:</b>", styles['Normal']), Paragraph("Java 17+, Java Swing GUI, JDBC, MySQL 8.0, IntelliJ IDEA", styles['Normal'])],
        [Paragraph("<b>Student Name:</b>", styles['Normal']), Paragraph("<b>Lucky Saini</b>", styles['Normal'])],
        [Paragraph("<b>Registration / Roll No.:</b>", styles['Normal']), Paragraph("<b>PIET35CR030</b>", styles['Normal'])],
        [Paragraph("<b>Project Guide:</b>", styles['Normal']), Paragraph("<b>Ms. Siddhi Kumari Sharma</b>", styles['Normal'])],
        [Paragraph("<b>Institution:</b>", styles['Normal']), Paragraph("Poornima Institute of Engineering & Technology", styles['Normal'])],
        [Paragraph("<b>Academic Session:</b>", styles['Normal']), Paragraph("2025 – 2026", styles['Normal'])],
    ]

    t_info = Table(student_info, colWidths=[180, printable_w - 180])
    t_info.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F8FAFC")),
        ('BOX', (0, 0), (-1, -1), 1.2, colors.HexColor("#CBD5E1")),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
        ('TOPPADDING', (0, 0), (-1, -1), 7),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 7),
        ('LEFTPADDING', (0, 0), (-1, -1), 12),
        ('RIGHTPADDING', (0, 0), (-1, -1), 12),
    ]))
    elements.append(t_info)

    elements.append(Spacer(1, 25))

    report_desc = (
        "<b>Document Overview:</b> This document constitutes <i>Chapter 5: Project Snapshots</i> "
        "of the Minor Project dissertation. The subsequent figures display authentic, pixel-accurate "
        "screen captures directly recorded from the fully functioning Java Swing desktop application "
        "connected live to MySQL database <code>faculty_research_repository</code>."
    )
    elements.append(Paragraph(report_desc, style_fig_desc))

    elements.append(PageBreak())

    # ==========================================
    # 2. SCREENSHOT PAGES
    # ==========================================
    screens = [
        {
            "id": "5.1",
            "file": "Figure_5_1_Login_Page.png",
            "title": "Figure 5.1: Login Page",
            "desc": "<b>Description:</b> The initial authentication interface of the Faculty Research Publication Repository. "
                    "Features secure role-based authentication, show/hide password toggle, and academic repository insignia. "
                    "Input validation prevents blank credentials and validates users against the MySQL <code>users</code> table.",
            "caption": "Figure 5.1: User Authentication and Login Interface"
        },
        {
            "id": "5.2",
            "file": "Figure_5_2_Home_Dashboard.png",
            "title": "Figure 5.2: Home / Dashboard",
            "desc": "<b>Description:</b> The administrative command center post-authentication for student user <b>Lucky Saini (Admin)</b>. "
                    "Displays 4 real-time metric cards (Total Faculty: 6, Total Publications: 10, Publications in 2026: 4, Departments: 4), "
                    "quick-action navigation buttons, full application JMenuBar, JToolBar, and recently indexed publication records.",
            "caption": "Figure 5.2: Administrative Home Dashboard with Live MySQL Metrics"
        },
        {
            "id": "5.3",
            "file": "Figure_5_3_Faculty_Registration_Page.png",
            "title": "Figure 5.3: Faculty Registration Page",
            "desc": "<b>Description:</b> The Faculty Profile Management module allowing registration of academic personnel. "
                    "Includes fields for Employee ID (unique validation), Full Name, Department dropdown, Academic Designation, "
                    "official Email, Contact Phone, and Research Specialization alongside live registered faculty records.",
            "caption": "Figure 5.3: Faculty Profile Registration and Management Interface"
        },
        {
            "id": "5.4",
            "file": "Figure_5_4_Add_Publication_Page.png",
            "title": "Figure 5.4: Add Publication Page",
            "desc": "<b>Description:</b> Dedicated form for cataloging new faculty research papers. "
                    "Captures author faculty, research paper title, publication category (Journal, Conference, Book Chapter, Patent), "
                    "indexing venue, research area, calendar year, DOI, ISSN/ISBN, publisher, publication status, web link, and full abstract.",
            "caption": "Figure 5.4: Research Publication Registration and Indexing Form"
        },
        {
            "id": "5.5",
            "file": "Figure_5_5_Publication_Repository.png",
            "title": "Figure 5.5: Publication Repository",
            "desc": "<b>Description:</b> Primary tabular publication repository module displaying all indexed institutional publications. "
                    "Features multi-attribute quick filtering by keyword, publication type, academic department, and publication status, "
                    "with one-click buttons for viewing complete details, refreshing, adding, updating, and deleting records.",
            "caption": "Figure 5.5: Central Research Publication Repository Tabular View"
        },
        {
            "id": "5.6",
            "file": "Figure_5_6_Search_Publication_Page.png",
            "title": "Figure 5.6: Search Publication Page",
            "desc": "<b>Description:</b> Advanced multi-criteria search and analytics laboratory. "
                    "Enables concurrent filtering across title keywords, author name, department, research domain, year, publication type, "
                    "and journal/conference venue using parameterized SQL queries with instant match count reporting.",
            "caption": "Figure 5.6: Advanced Multi-Attribute Publication Search Interface"
        },
        {
            "id": "5.7",
            "file": "Figure_5_7_Update_Publication_Page.png",
            "title": "Figure 5.7: Update Publication Page",
            "desc": "<b>Description:</b> Publication modification interface loaded with an existing publication record "
                    "(<i>Deep Transformer Architectures for Autonomous Decision Systems</i>, Dr. John Doe). "
                    "Allows authorized administrators to update publication metadata, status, DOI, and abstract with immediate MySQL persistence.",
            "caption": "Figure 5.7: Research Publication Edit and Update Screen"
        },
        {
            "id": "5.8",
            "file": "Figure_5_8_Delete_Publication_Page.png",
            "title": "Figure 5.8: Delete Publication / Management Page",
            "desc": "<b>Description:</b> Deletion and records management interface demonstrating security confirmation. "
                    "When an administrator selects a publication for deletion, a modal confirmation dialog is prompted to prevent "
                    "accidental record loss, ensuring referential integrity with the underlying database.",
            "caption": "Figure 5.8: Publication Deletion Confirmation and Record Management"
        },
        {
            "id": "5.9",
            "file": "Figure_5_9_Faculty_Profile_Details.png",
            "title": "Figure 5.9: Faculty Profile / Details",
            "desc": "<b>Description:</b> Comprehensive faculty profile inspection interface. "
                    "Selecting a faculty member from the directory automatically populates their full institutional profile, "
                    "departmental affiliation, rank, official contact details, and specialized research domains.",
            "caption": "Figure 5.9: Faculty Member Profile and Academic Details Inspection"
        },
        {
            "id": "5.10",
            "file": "Figure_5_10_Settings_Change_Password.png",
            "title": "Figure 5.10: Settings / Change Password",
            "desc": "<b>Description:</b> Account security and credential maintenance dialog. "
                    "Allows authenticated users to update their login password after verifying current credentials against the MySQL database, "
                    "with password strength and confirmation consistency validation.",
            "caption": "Figure 5.10: User Account Settings and Password Modification Dialog"
        },
        {
            "id": "5.11",
            "file": "Figure_5_11_Database_Record_Details.png",
            "title": "Figure 5.11: Database Record Details / Publication Viewer",
            "desc": "<b>Description:</b> Comprehensive database record inspection viewer. "
                    "Presents all normalized relational database fields joined across faculty and publication tables, "
                    "including publisher, DOI, ISSN/ISBN, date added, abstract text, and direct browser integration.",
            "caption": "Figure 5.11: Detailed Database Record Viewer with Abstract and Metadata"
        }
    ]

    for item in screens:
        img_path = os.path.join("screenshots", item["file"])
        if not os.path.exists(img_path):
            print(f"Skipping {item['file']} (Not found)")
            continue

        # Load image with PIL to calculate exact proportional scaling
        with PILImage.open(img_path) as pil_img:
            orig_w, orig_h = pil_img.size

        # Fit within printable width and available vertical space
        max_img_w = printable_w
        max_img_h = printable_h - 85 # leave room for title, description and caption

        scale = min(max_img_w / orig_w, max_img_h / orig_h)
        disp_w = orig_w * scale
        disp_h = orig_h * scale

        elements.append(Paragraph(item["title"], style_fig_title))
        elements.append(Paragraph(item["desc"], style_fig_desc))
        elements.append(Spacer(1, 4))

        # Centered Image Table
        img_element = RLImage(img_path, width=disp_w, height=disp_h)
        t_img = Table([[img_element]], colWidths=[printable_w])
        t_img.setStyle(TableStyle([
            ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
            ('TOPPADDING', (0, 0), (-1, -1), 0),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 0),
        ]))
        elements.append(t_img)

        elements.append(Paragraph(f"<b>{item['caption']}</b>", style_fig_caption))
        elements.append(PageBreak())

    # Build the document using NumberedCanvas
    doc.build(elements, canvasmaker=NumberedCanvas)
    print(f"PDF successfully created: {output_pdf_path} (Size: {os.path.getsize(output_pdf_path)} bytes)")


if __name__ == "__main__":
    pdf_name = "Faculty_Research_Publication_Repository_Project_Screenshots_Lucky_Saini.pdf"
    
    # 1. Output to project directory
    local_pdf = os.path.abspath(pdf_name)
    build_pdf(local_pdf)

    # 2. Output to Desktop
    desktop_pdf = os.path.expanduser(r"~\Desktop\\" + pdf_name)
    try:
        import shutil
        shutil.copy2(local_pdf, desktop_pdf)
        print(f"Copied to Desktop: {desktop_pdf}")
    except Exception as e:
        print(f"Could not copy to Desktop: {e}")
