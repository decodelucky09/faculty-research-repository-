package com.facultyrepository.model;

import java.sql.Timestamp;

/**
 * Model class representing a Faculty member / researcher.
 */
public class Faculty {
    private int facultyId;
    private String employeeId;
    private String facultyName;
    private String department;
    private String designation;
    private String email;
    private String phone;
    private String specialization;
    private Timestamp createdAt;

    public Faculty() {
    }

    public Faculty(int facultyId, String employeeId, String facultyName, String department,
                   String designation, String email, String phone, String specialization) {
        this.facultyId = facultyId;
        this.employeeId = employeeId;
        this.facultyName = facultyName;
        this.department = department;
        this.designation = designation;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
    }

    public Faculty(String employeeId, String facultyName, String department,
                   String designation, String email, String phone, String specialization) {
        this.employeeId = employeeId;
        this.facultyName = facultyName;
        this.department = department;
        this.designation = designation;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return facultyName + " (" + employeeId + " - " + department + ")";
    }
}
