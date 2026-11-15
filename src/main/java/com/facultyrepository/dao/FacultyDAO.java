package com.facultyrepository.dao;

import com.facultyrepository.db.DatabaseConnection;
import com.facultyrepository.model.Faculty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Faculty CRUD operations and metrics.
 */
public class FacultyDAO {

    /**
     * Adds a new Faculty record.
     */
    public boolean addFaculty(Faculty f) throws SQLException {
        String sql = "INSERT INTO faculty (employee_id, faculty_name, department, designation, email, phone, specialization) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getEmployeeId().trim());
            ps.setString(2, f.getFacultyName().trim());
            ps.setString(3, f.getDepartment().trim());
            ps.setString(4, f.getDesignation().trim());
            ps.setString(5, f.getEmail().trim());
            ps.setString(6, f.getPhone() != null ? f.getPhone().trim() : null);
            ps.setString(7, f.getSpecialization() != null ? f.getSpecialization().trim() : null);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        f.setFacultyId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Updates an existing Faculty record.
     */
    public boolean updateFaculty(Faculty f) throws SQLException {
        String sql = "UPDATE faculty SET employee_id = ?, faculty_name = ?, department = ?, " +
                     "designation = ?, email = ?, phone = ?, specialization = ? WHERE faculty_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getEmployeeId().trim());
            ps.setString(2, f.getFacultyName().trim());
            ps.setString(3, f.getDepartment().trim());
            ps.setString(4, f.getDesignation().trim());
            ps.setString(5, f.getEmail().trim());
            ps.setString(6, f.getPhone() != null ? f.getPhone().trim() : null);
            ps.setString(7, f.getSpecialization() != null ? f.getSpecialization().trim() : null);
            ps.setInt(8, f.getFacultyId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a Faculty record by ID.
     * Note: database foreign key constraint will cascade delete associated publications.
     */
    public boolean deleteFaculty(int facultyId) throws SQLException {
        String sql = "DELETE FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all Faculty records.
     */
    public List<Faculty> getAllFaculty() throws SQLException {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT faculty_id, employee_id, faculty_name, department, designation, email, phone, specialization, created_at " +
                     "FROM faculty ORDER BY faculty_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapFaculty(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves a Faculty record by ID.
     */
    public Faculty getFacultyById(int facultyId) throws SQLException {
        String sql = "SELECT faculty_id, employee_id, faculty_name, department, designation, email, phone, specialization, created_at " +
                     "FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFaculty(rs);
                }
            }
        }
        return null;
    }

    /**
     * Searches faculty by keyword across name, employee ID, department, designation, and specialization.
     */
    public List<Faculty> searchFaculty(String keyword) throws SQLException {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT faculty_id, employee_id, faculty_name, department, designation, email, phone, specialization, created_at " +
                     "FROM faculty WHERE faculty_name LIKE ? OR employee_id LIKE ? OR department LIKE ? " +
                     "OR designation LIKE ? OR specialization LIKE ? ORDER BY faculty_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFaculty(rs));
                }
            }
        }
        return list;
    }

    /**
     * Checks if an employee ID already exists for another faculty record.
     */
    public boolean isEmployeeIdTaken(String employeeId, int excludeFacultyId) throws SQLException {
        String sql = "SELECT faculty_id FROM faculty WHERE employee_id = ? AND faculty_id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId.trim());
            ps.setInt(2, excludeFacultyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Returns total count of registered faculty members.
     */
    public int getTotalFacultyCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM faculty";
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
     * Returns count of distinct departments represented.
     */
    public int getDistinctDepartmentCount() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT department) FROM faculty";
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
     * Returns a list of distinct department names.
     */
    public List<String> getAllDepartments() throws SQLException {
        List<String> depts = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM faculty WHERE department IS NOT NULL AND department <> '' ORDER BY department ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                depts.add(rs.getString(1));
            }
        }
        return depts;
    }

    private Faculty mapFaculty(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setFacultyId(rs.getInt("faculty_id"));
        f.setEmployeeId(rs.getString("employee_id"));
        f.setFacultyName(rs.getString("faculty_name"));
        f.setDepartment(rs.getString("department"));
        f.setDesignation(rs.getString("designation"));
        f.setEmail(rs.getString("email"));
        f.setPhone(rs.getString("phone"));
        f.setSpecialization(rs.getString("specialization"));
        f.setCreatedAt(rs.getTimestamp("created_at"));
        return f;
    }
}
