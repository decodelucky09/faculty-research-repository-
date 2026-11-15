package com.facultyrepository.model;

import java.sql.Timestamp;

/**
 * Model class representing a research publication.
 * Includes join fields for facultyName and department for display in JTables.
 */
public class Publication {
    private int publicationId;
    private int facultyId;
    private String title;
    private String publicationType;
    private String journalConference;
    private String researchArea;
    private int publicationYear;
    private String doi;
    private String issnIsbn;
    private String publisher;
    private String publicationStatus;
    private String publicationLink;
    private String abstractText;
    private Timestamp dateAdded;

    // Joined fields from faculty table for display convenience
    private String facultyName;
    private String department;

    public Publication() {
    }

    public Publication(int publicationId, int facultyId, String title, String publicationType,
                       String journalConference, String researchArea, int publicationYear,
                       String doi, String issnIsbn, String publisher, String publicationStatus,
                       String publicationLink, String abstractText) {
        this.publicationId = publicationId;
        this.facultyId = facultyId;
        this.title = title;
        this.publicationType = publicationType;
        this.journalConference = journalConference;
        this.researchArea = researchArea;
        this.publicationYear = publicationYear;
        this.doi = doi;
        this.issnIsbn = issnIsbn;
        this.publisher = publisher;
        this.publicationStatus = publicationStatus;
        this.publicationLink = publicationLink;
        this.abstractText = abstractText;
    }

    public Publication(int facultyId, String title, String publicationType,
                       String journalConference, String researchArea, int publicationYear,
                       String doi, String issnIsbn, String publisher, String publicationStatus,
                       String publicationLink, String abstractText) {
        this.facultyId = facultyId;
        this.title = title;
        this.publicationType = publicationType;
        this.journalConference = journalConference;
        this.researchArea = researchArea;
        this.publicationYear = publicationYear;
        this.doi = doi;
        this.issnIsbn = issnIsbn;
        this.publisher = publisher;
        this.publicationStatus = publicationStatus;
        this.publicationLink = publicationLink;
        this.abstractText = abstractText;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }

    public String getJournalConference() {
        return journalConference;
    }

    public void setJournalConference(String journalConference) {
        this.journalConference = journalConference;
    }

    public String getResearchArea() {
        return researchArea;
    }

    public void setResearchArea(String researchArea) {
        this.researchArea = researchArea;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getIssnIsbn() {
        return issnIsbn;
    }

    public void setIssnIsbn(String issnIsbn) {
        this.issnIsbn = issnIsbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(String publicationStatus) {
        this.publicationStatus = publicationStatus;
    }

    public String getPublicationLink() {
        return publicationLink;
    }

    public void setPublicationLink(String publicationLink) {
        this.publicationLink = publicationLink;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
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

    @Override
    public String toString() {
        return title + " (" + publicationYear + ") - " + facultyName;
    }
}
