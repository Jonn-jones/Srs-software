package com.spas.crss.service;

import com.spas.crss.dao.CourseAssignmentDAO;
import com.spas.crss.model.CourseAssignment;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class CourseAssignmentService {

    private final CourseAssignmentDAO assignmentDAO;

    public CourseAssignmentService() {
        this.assignmentDAO = new CourseAssignmentDAO();
    }

    // ASSIGN A COURSE TO A LECTURER
    public boolean assignCourse(
            UserAccount user,
            CourseAssignment assignment) {

        checkPermission(user);
        validateAssignment(assignment);

        if (assignment.getLecturerId() != null) {
            assignment.setLecturerId(
                    assignment.getLecturerId().trim()
            );
        }

        return assignmentDAO.assignCourse(assignment);
    }

    // RETRIEVE ALL COURSE ASSIGNMENTS
    public List<CourseAssignment> getAllAssignments(UserAccount user) {

        checkPermission(user);

        return assignmentDAO.getAllAssignments();
    }

    // RETRIEVE ASSIGNMENTS BY LECTURER
    public List<CourseAssignment> getAssignmentsByLecturer(
            UserAccount user,
            String lecturerId) {

        checkPermission(user);
        validateLecturerId(lecturerId);

        return assignmentDAO.getAssignmentsByLecturer(
                lecturerId.trim()
        );
    }

    // REMOVE A COURSE ASSIGNMENT
    public boolean removeAssignment(
            UserAccount user,
            int assignmentId) {

        checkPermission(user);
        validateAssignmentId(assignmentId);

        return assignmentDAO.removeAssignment(assignmentId);
    }

    // VALIDATE ASSIGNMENT DETAILS
    private void validateAssignment(CourseAssignment assignment) {

        if (assignment == null) {
            throw new IllegalArgumentException(
                    "Course assignment details cannot be null."
            );
        }

        if (assignment.getCourseId() <= 0) {
            throw new IllegalArgumentException(
                    "Course ID must be a positive number."
            );
        }

        validateLecturerId(assignment.getLecturerId());

        if (assignment.getSemesterId() <= 0) {
            throw new IllegalArgumentException(
                    "Semester ID must be a positive number."
            );
        }
    }

    // VALIDATE LECTURER ID
    private void validateLecturerId(String lecturerId) {

        if (lecturerId == null || lecturerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Lecturer ID is required."
            );
        }
    }

    // VALIDATE ASSIGNMENT ID
    private void validateAssignmentId(int assignmentId) {

        if (assignmentId <= 0) {
            throw new IllegalArgumentException(
                    "Assignment ID must be a positive number."
            );
        }
    }

    // CHECK PERMISSIONS
    private void checkPermission(UserAccount user) {

        if (!AccessControl.hasPermission(
                user,
                Permission.MANAGE_COURSE_ASSIGNMENTS)) {

            throw new SecurityException(
                    "You are not authorized to manage course assignments."
            );
        }
    }
}
