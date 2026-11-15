package com.facultyrepository.dao;

import com.facultyrepository.db.DatabaseConnection;
import com.facultyrepository.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User authentication and user management.
 */
public class UserDAO {

    /**
     * Authenticates a user against MySQL.
     *
     * @param username user login name
     * @param password plain text password
     * @return User object if valid, null otherwise
     * @throws SQLException on database error
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role, full_name, email, created_at " +
                     "FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Verifies if the supplied current password matches the user's password in MySQL.
     */
    public boolean verifyCurrentPassword(int userId, String currentPassword) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, currentPassword);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Updates user password.
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Checks if a username is already registered.
     */
    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserts a new user into MySQL.
     */
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, full_name, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName().trim());
            ps.setString(5, user.getEmail().trim());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all users.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, role, full_name, email, created_at FROM users ORDER BY user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(u);
            }
        }
        return list;
    }
}
