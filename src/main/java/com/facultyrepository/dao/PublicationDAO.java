package com.facultyrepository.dao;

import com.facultyrepository.db.DatabaseConnection;
import com.facultyrepository.model.Publication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Publication CRUD operations, multi-criteria filtering, and statistics.
 */
public class PublicationDAO {

    private static final String BASE_SELECT =
            "SELECT p.publication_id, p.faculty_id, p.title, p.publication_type, " +
            "p.journal_conference, p.research_area, p.publication_year, p.doi, " +
            "p.issn_isbn, p.publisher, p.publication_status, p.publication_link, " +
            "p.abstract, p.date_added, f.faculty_name, f.department " +
            "FROM publications p " +
            "INNER JOIN faculty f ON p.faculty_id = f.faculty_id ";

    /**
     * Inserts a new Publication record.
     */
    public boolean addPublication(Publication p) throws SQLException {
        String sql = "INSERT INTO publications (faculty_id, title, publication_type, journal_conference, " +
                     "research_area, publication_year, doi, issn_isbn, publisher, publication_status, " +
                     "publication_link, abstract) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getFacultyId());
            ps.setString(2, p.getTitle().trim());
            ps.setString(3, p.getPublicationType());
            ps.setString(4, p.getJournalConference().trim());
            ps.setString(5, p.getResearchArea().trim());
            ps.setInt(6, p.getPublicationYear());
            ps.setString(7, p.getDoi() != null && !p.getDoi().trim().isEmpty() ? p.getDoi().trim() : null);
            ps.setString(8, p.getIssnIsbn() != null && !p.getIssnIsbn().trim().isEmpty() ? p.getIssnIsbn().trim() : null);
            ps.setString(9, p.getPublisher() != null && !p.getPublisher().trim().isEmpty() ? p.getPublisher().trim() : null);
            ps.setString(10, p.getPublicationStatus());
            ps.setString(11, p.getPublicationLink() != null && !p.getPublicationLink().trim().isEmpty() ? p.getPublicationLink().trim() : null);
            ps.setString(12, p.getAbstractText() != null && !p.getAbstractText().trim().isEmpty() ? p.getAbstractText().trim() : null);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setPublicationId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Updates an existing Publication record.
     */
    public boolean updatePublication(Publication p) throws SQLException {
        String sql = "UPDATE publications SET faculty_id = ?, title = ?, publication_type = ?, " +
                     "journal_conference = ?, research_area = ?, publication_year = ?, doi = ?, " +
                     "issn_isbn = ?, publisher = ?, publication_status = ?, publication_link = ?, " +
                     "abstract = ? WHERE publication_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getFacultyId());
            ps.setString(2, p.getTitle().trim());
            ps.setString(3, p.getPublicationType());
            ps.setString(4, p.getJournalConference().trim());
            ps.setString(5, p.getResearchArea().trim());
            ps.setInt(6, p.getPublicationYear());
            ps.setString(7, p.getDoi() != null && !p.getDoi().trim().isEmpty() ? p.getDoi().trim() : null);
            ps.setString(8, p.getIssnIsbn() != null && !p.getIssnIsbn().trim().isEmpty() ? p.getIssnIsbn().trim() : null);
            ps.setString(9, p.getPublisher() != null && !p.getPublisher().trim().isEmpty() ? p.getPublisher().trim() : null);
            ps.setString(10, p.getPublicationStatus());
            ps.setString(11, p.getPublicationLink() != null && !p.getPublicationLink().trim().isEmpty() ? p.getPublicationLink().trim() : null);
            ps.setString(12, p.getAbstractText() != null && !p.getAbstractText().trim().isEmpty() ? p.getAbstractText().trim() : null);
            ps.setInt(13, p.getPublicationId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a Publication record by ID.
     */
    public boolean deletePublication(int publicationId) throws SQLException {
        String sql = "DELETE FROM publications WHERE publication_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, publicationId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all publication records with faculty details.
     */
    public List<Publication> getAllPublications() throws SQLException {
        List<Publication> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY p.publication_year DESC, p.publication_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapPublication(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves a single publication by its ID.
     */
    public Publication getPublicationById(int publicationId) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.publication_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPublication(rs);
                }
            }
        }
        return null;
    }

    /**
     * Performs dynamic multi-criteria search and filtering.
     */
    public List<Publication> searchAndFilter(String keyword, String authorName, String department,
                                             String researchArea, Integer year, String publicationType,
                                             String journalConference, String status) throws SQLException {
        List<Publication> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.title LIKE ? OR p.journal_conference LIKE ? OR p.abstract LIKE ? OR p.doi LIKE ?) ");
            String pat = "%" + keyword.trim() + "%";
            params.add(pat);
            params.add(pat);
            params.add(pat);
            params.add(pat);
        }
        if (authorName != null && !authorName.trim().isEmpty()) {
            sql.append("AND f.faculty_name LIKE ? ");
            params.add("%" + authorName.trim() + "%");
        }
        if (department != null && !department.trim().isEmpty() && !"All Departments".equalsIgnoreCase(department)) {
            sql.append("AND f.department = ? ");
            params.add(department.trim());
        }
        if (researchArea != null && !researchArea.trim().isEmpty() && !"All Areas".equalsIgnoreCase(researchArea)) {
            sql.append("AND p.research_area LIKE ? ");
            params.add("%" + researchArea.trim() + "%");
        }
        if (year != null && year > 0) {
            sql.append("AND p.publication_year = ? ");
            params.add(year);
        }
        if (publicationType != null && !publicationType.trim().isEmpty() && !"All Types".equalsIgnoreCase(publicationType)) {
            sql.append("AND p.publication_type = ? ");
            params.add(publicationType.trim());
        }
        if (journalConference != null && !journalConference.trim().isEmpty()) {
            sql.append("AND p.journal_conference LIKE ? ");
            params.add("%" + journalConference.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty() && !"All Statuses".equalsIgnoreCase(status)) {
            sql.append("AND p.publication_status = ? ");
            params.add(status.trim());
        }

        sql.append("ORDER BY p.publication_year DESC, p.publication_id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object val = params.get(i);
                if (val instanceof String) {
                    ps.setString(i + 1, (String) val);
                } else if (val instanceof Integer) {
                    ps.setInt(i + 1, (Integer) val);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPublication(rs));
                }
            }
        }
        return list;
    }

    /**
     * Returns total count of registered publications.
     */
    public int getTotalPublicationCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM publications";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Returns total publications published in the specified year.
     */
    public int getPublicationsCountByYear(int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM publications WHERE publication_year = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Retrieves recent publications (limit N).
     */
    public List<Publication> getRecentPublications(int limit) throws SQLException {
        List<Publication> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY p.date_added DESC, p.publication_id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPublication(rs));
                }
            }
        }
        return list;
    }

    /**
     * Distinct research areas for dropdown filter.
     */
    public List<String> getDistinctResearchAreas() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT research_area FROM publications WHERE research_area IS NOT NULL AND research_area <> '' ORDER BY research_area ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        return list;
    }

    private Publication mapPublication(ResultSet rs) throws SQLException {
        Publication p = new Publication();
        p.setPublicationId(rs.getInt("publication_id"));
        p.setFacultyId(rs.getInt("faculty_id"));
        p.setTitle(rs.getString("title"));
        p.setPublicationType(rs.getString("publication_type"));
        p.setJournalConference(rs.getString("journal_conference"));
        p.setResearchArea(rs.getString("research_area"));
        p.setPublicationYear(rs.getInt("publication_year"));
        p.setDoi(rs.getString("doi"));
        p.setIssnIsbn(rs.getString("issn_isbn"));
        p.setPublisher(rs.getString("publisher"));
        p.setPublicationStatus(rs.getString("publication_status"));
        p.setPublicationLink(rs.getString("publication_link"));
        p.setAbstractText(rs.getString("abstract"));
        p.setDateAdded(rs.getTimestamp("date_added"));
        p.setFacultyName(rs.getString("faculty_name"));
        p.setDepartment(rs.getString("department"));
        return p;
    }
}
