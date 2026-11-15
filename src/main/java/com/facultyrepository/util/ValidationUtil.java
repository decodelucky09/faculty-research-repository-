package com.facultyrepository.util;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Reusable validation utility for form input validation.
 * Displays user-friendly alerts and prevents invalid data from reaching MySQL.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+]?[0-9\\s\\-()]{7,20}$");

    private static final Pattern DOI_PATTERN =
            Pattern.compile("^10\\.\\d{4,9}/[-._;()/:A-Za-z0-9]+$");

    /**
     * Checks if a string is non-null and not blank.
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates that a required text field is filled.
     */
    public static boolean validateRequired(Component parent, String text, String fieldName) {
        if (!isNotEmpty(text)) {
            showWarning(parent, fieldName + " cannot be empty!");
            return false;
        }
        return true;
    }

    /**
     * Validates email format.
     */
    public static boolean validateEmail(Component parent, String email) {
        if (!isNotEmpty(email)) {
            showWarning(parent, "Email address cannot be empty!");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            showWarning(parent, "Please enter a valid email address (e.g. name@university.edu)!");
            return false;
        }
        return true;
    }

    /**
     * Validates phone number format if provided (optional).
     */
    public static boolean validatePhone(Component parent, String phone) {
        if (isNotEmpty(phone) && !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            showWarning(parent, "Please enter a valid phone number (digits, optional '+', '-', spaces)!");
            return false;
        }
        return true;
    }

    /**
     * Validates publication year (positive integer between 1950 and current + 5).
     */
    public static boolean validateYear(Component parent, String yearStr) {
        if (!isNotEmpty(yearStr)) {
            showWarning(parent, "Publication Year cannot be empty!");
            return false;
        }
        try {
            int year = Integer.parseInt(yearStr.trim());
            int currentYear = java.time.Year.now().getValue();
            if (year < 1950 || year > currentYear + 2) {
                showWarning(parent, "Publication Year must be between 1950 and " + (currentYear + 2) + "!");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showWarning(parent, "Publication Year must be a valid 4-digit number (e.g. 2026)!");
            return false;
        }
    }

    /**
     * Validates DOI format if provided.
     */
    public static boolean validateDOI(Component parent, String doi) {
        if (isNotEmpty(doi) && !DOI_PATTERN.matcher(doi.trim()).matches()) {
            showWarning(parent, "DOI format is invalid. Standard format: 10.xxxx/yyyy (e.g. 10.1109/TPAMI.2026.1045678)!");
            return false;
        }
        return true;
    }

    /**
     * Validates password match and minimum length.
     */
    public static boolean validatePasswordChange(Component parent, String currentPassword,
                                                 String newPassword, String confirmPassword) {
        if (!isNotEmpty(currentPassword)) {
            showWarning(parent, "Current password is required!");
            return false;
        }
        if (!isNotEmpty(newPassword)) {
            showWarning(parent, "New password cannot be empty!");
            return false;
        }
        if (newPassword.length() < 6) {
            showWarning(parent, "New password must be at least 6 characters long!");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            showWarning(parent, "New password and Confirm password do not match!");
            return false;
        }
        return true;
    }

    /**
     * Helper to show warning alert dialog.
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Input Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Helper to show success alert dialog.
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Helper to show error alert dialog.
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "System Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Helper to confirm deletion or destructive action.
     */
    public static boolean confirmAction(Component parent, String message, String title) {
        int choice = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
}
