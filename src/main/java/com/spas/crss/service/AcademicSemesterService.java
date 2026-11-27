package com.spas.crss.service;

import com.spas.crss.dao.AcademicSemesterDAO;
import com.spas.crss.model.AcademicSemester;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class AcademicSemesterService {

    private final AcademicSemesterDAO semesterDAO;

    public AcademicSemesterService() {
        this.semesterDAO = new AcademicSemesterDAO();
    }

    // 1. ADD SEMESTER
    public int addSemester(UserAccount user, AcademicSemester semester) {

        if (!AccessControl.hasPermission(user, Permission.MANAGE_SEMESTERS)) {
            throw new SecurityException("You are not authorized to manage semesters.");
        }

        if (semester == null
                || semester.getAcademicYear() == null
                || semester.getAcademicYear().isBlank()
                || semester.getSemesterName() == null
                || semester.getSemesterName().isBlank()
                || semester.getStartDate() == null
                || semester.getEndDate() == null) {
            throw new IllegalArgumentException("All required semester fields must be provided.");
        }

        if (semester.getEndDate().before(semester.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        if (semester.getSemesterStatus() == null
                || semester.getSemesterStatus().isBlank()) {
            semester.setSemesterStatus("Upcoming");
        }

        return semesterDAO.addSemester(semester);
    }

    // 2. VIEW ALL SEMESTERS
    public List<AcademicSemester> getAllSemesters(UserAccount user) {

        if (!AccessControl.hasPermission(user, Permission.MANAGE_SEMESTERS)) {
            throw new SecurityException("You are not authorized to view semesters.");
        }

        return semesterDAO.getAllSemester();
    }

    // 3. UPDATE SEMESTER
    public boolean updateSemester(UserAccount user, AcademicSemester semester) {

        if (!AccessControl.hasPermission(user, Permission.MANAGE_SEMESTERS)) {
            throw new SecurityException("You are not authorized to manage semesters.");
        }

        if (semester == null || semester.getSemesterId() <= 0) {
            throw new IllegalArgumentException("A valid semester ID is required.");
        }

        if (semester.getAcademicYear() == null
                || semester.getAcademicYear().isBlank()
                || semester.getSemesterName() == null
                || semester.getSemesterName().isBlank()
                || semester.getStartDate() == null
                || semester.getEndDate() == null) {
            throw new IllegalArgumentException("All required semester fields must be provided.");
        }

        if (semester.getEndDate().before(semester.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        return semesterDAO.updateSemester(semester);
    }

    // 4. DELETE SEMESTER
    public boolean deleteSemester(UserAccount user, int semesterId) {

        if (!AccessControl.hasPermission(user, Permission.MANAGE_SEMESTERS)) {
            throw new SecurityException("You are not authorized to delete semesters.");
        }

        if (semesterId <= 0) {
            throw new IllegalArgumentException("Invalid semester ID.");
        }

        return semesterDAO.deleteSemester(semesterId);
    }

    // 5. ACTIVATE SEMESTER
    public boolean activateSemester(UserAccount user, int semesterId) {

        if (!AccessControl.hasPermission(user, Permission.MANAGE_SEMESTERS)) {
            throw new SecurityException("You are not authorized to activate semesters.");
        }

        if (semesterId <= 0) {
            throw new IllegalArgumentException("Invalid semester ID.");
        }

        return semesterDAO.activateSemester(semesterId);
    }

    // 6. GET ACTIVE SEMESTER
    public AcademicSemester getActiveSemester(UserAccount user) {

        if (!AccessControl.hasPermission(user, Permission.MANAGE_SEMESTERS)) {
            throw new SecurityException("You are not authorized to view the active semester.");
        }

        return semesterDAO.getActiveSemester();
    }
}
