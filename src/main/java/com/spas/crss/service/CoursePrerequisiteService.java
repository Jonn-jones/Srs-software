package com.spas.crss.service;

import com.spas.crss.dao.CoursePrerequisiteDAO;
import com.spas.crss.model.CoursePrerequisite;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class CoursePrerequisiteService {

    private final CoursePrerequisiteDAO prerequisiteDAO;
    private final AccessControl accessControl;

    public CoursePrerequisiteService() {
        this.prerequisiteDAO = new CoursePrerequisiteDAO();
        this.accessControl = new AccessControl();
    }

    public boolean addPrerequisite(
            UserAccount user,
            CoursePrerequisite prerequisite) {

        if (!accessControl.hasPermission(
                user, Permission.MANAGE_PREREQUISITES)) {
            throw new SecurityException(
                    "You do not have permission to manage prerequisites.");
        }

        if (prerequisite == null
                || prerequisite.getCourseId() <= 0
                || prerequisite.getPrerequisiteCourseId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid course and prerequisite course IDs are required.");
        }

        if (prerequisite.getCourseId()
                == prerequisite.getPrerequisiteCourseId()) {
            throw new IllegalArgumentException(
                    "A course cannot be its own prerequisite.");
        }

        return prerequisiteDAO.addPrerequisite(prerequisite);
    }

    public List<CoursePrerequisite> getPrerequisites(
            UserAccount user, int courseId) {

        if (user == null) {
            throw new SecurityException("Authentication is required.");
        }

        if (courseId <= 0) {
            throw new IllegalArgumentException(
                    "A valid course ID is required.");
        }

        return prerequisiteDAO.getPrerequisites(courseId);
    }

    public boolean removePrerequisite(
            UserAccount user,
            int courseId,
            int prerequisiteCourseId) {

        if (!accessControl.hasPermission(
                user, Permission.MANAGE_PREREQUISITES)) {
            throw new SecurityException(
                    "You do not have permission to manage prerequisites.");
        }

        if (courseId <= 0 || prerequisiteCourseId <= 0) {
            throw new IllegalArgumentException(
                    "Valid course and prerequisite course IDs are required.");
        }

        return prerequisiteDAO.removePrerequisite(
                courseId, prerequisiteCourseId);
    }

    public boolean hasPassedPrerequisite(
            UserAccount user,
            String studentId,
            int prerequisiteCourseId) {

        if (user == null) {
            throw new SecurityException("Authentication is required.");
        }

        if (studentId == null || studentId.trim().isEmpty()
                || prerequisiteCourseId <= 0) {
            throw new IllegalArgumentException(
                    "A valid student ID and prerequisite course ID are required.");
        }

        return prerequisiteDAO.hasPassedPrerequisite(
                studentId, prerequisiteCourseId);
    }

    public boolean canTakeCourse(
            UserAccount user,
            String studentId,
            int courseId) {

        if (user == null) {
            throw new SecurityException("Authentication is required.");
        }

        if (studentId == null || studentId.trim().isEmpty()
                || courseId <= 0) {
            throw new IllegalArgumentException(
                    "A valid student ID and course ID are required.");
        }

        return prerequisiteDAO.canTakeCourse(studentId, courseId);
    }
}
