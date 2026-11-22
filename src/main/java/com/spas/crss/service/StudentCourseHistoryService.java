package com.spas.crss.service;

import com.spas.crss.dao.StudentCourseHistoryDAO;
import com.spas.crss.model.StudentCourseHistory;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class StudentCourseHistoryService {

    private final StudentCourseHistoryDAO historyDAO;

    public StudentCourseHistoryService() {
        this.historyDAO = new StudentCourseHistoryDAO();
    }

    // Student views their own academic history
    public List<StudentCourseHistory> viewMyHistory(UserAccount user) {

        if (!AccessControl.hasPermission(
                user, Permission.VIEW_OWN_COURSE_HISTORY)) {
            throw new SecurityException(
                    "You are not authorized to view course history.");
        }

        if (user.getStudentId() == null) {
            throw new IllegalArgumentException(
                    "This account is not linked to a student.");
        }

        return historyDAO.getHistoryByStudent(user.getStudentId());
    }

    // CoD views a particular student's academic history
    public List<StudentCourseHistory> viewStudentHistory(
            UserAccount user, String studentId) {

        checkManagementPermission(user);

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Student ID is required.");
        }

        return historyDAO.getHistoryByStudent(studentId);
    }

    // CoD records a student's academic result
    public boolean addHistory(
            UserAccount user, StudentCourseHistory history) {

        checkManagementPermission(user);

        if (history == null) {
            throw new IllegalArgumentException(
                    "Course history record cannot be null.");
        }

        return historyDAO.addHistory(history);
    }

    // CoD updates an existing academic result
    public boolean updateResult(
            UserAccount user, int historyId, boolean passed) {

        checkManagementPermission(user);

        if (historyId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid history ID.");
        }

        return historyDAO.updateResult(historyId, passed);
    }

    // Checks whether a student has passed a particular course
    public boolean hasPassedCourse(
            UserAccount user, String studentId, int courseId) {

        if (user == null) {
            throw new SecurityException("Authentication required.");
        }

        boolean isStudent =
                AccessControl.hasPermission(
                        user, Permission.VIEW_OWN_COURSE_HISTORY)
                && studentId != null
                && studentId.equals(user.getStudentId());

        boolean isCoD =
                AccessControl.hasPermission(
                        user, Permission.MANAGE_STUDENT_COURSE_HISTORY);

        if (!isStudent && !isCoD) {
            throw new SecurityException(
                    "You are not authorized to access this course history.");
        }

        if (studentId == null || studentId.isBlank() || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Valid student ID and course ID are required.");
        }

        return historyDAO.hasPassedCourse(studentId, courseId);
    }

    private void checkManagementPermission(UserAccount user) {

        if (!AccessControl.hasPermission(
                user, Permission.MANAGE_STUDENT_COURSE_HISTORY)) {
            throw new SecurityException(
                    "Only authorized academic administrators can manage course history.");
        }
    }
}
