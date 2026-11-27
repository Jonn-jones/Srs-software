package com.spas.crss.service;

import com.spas.crss.dao.CourseDAO;
import com.spas.crss.model.Course;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class CourseManagementService {

    private final CourseDAO courseDAO;

    public CourseManagementService() {
        this.courseDAO = new CourseDAO();
    }

    // CREATE
    public boolean addCourse(UserAccount user, Course course) {
        checkPermission(user);
        validateCourse(course);

        if (courseCodeExists(course.getCourseCode(), null)) {
            throw new IllegalArgumentException(
                    "A course with this code already exists."
            );
        }

        return courseDAO.addCourse(course);
    }

    // READ ONE
    public Course getCourseById(UserAccount user, int courseId) {
        checkPermission(user);
        validateCourseId(courseId);

        return courseDAO.getCourseById(courseId);
    }

    // READ ALL
    public List<Course> getAllCourses(UserAccount user) {
        checkPermission(user);

        return courseDAO.getAllCourses();
    }

    // UPDATE
    public boolean updateCourse(UserAccount user, Course course) {
        checkPermission(user);
        validateCourse(course);

        if (courseDAO.getCourseById(course.getCourseId()) == null) {
            throw new IllegalArgumentException("Course does not exist.");
        }

        if (courseCodeExists(course.getCourseCode(), course.getCourseId())) {
            throw new IllegalArgumentException(
                    "Another course already uses this course code."
            );
        }

        return courseDAO.updateCourse(course);
    }

    // DELETE
    public boolean deleteCourse(UserAccount user, int courseId) {
        checkPermission(user);
        validateCourseId(courseId);

        if (courseDAO.getCourseById(courseId) == null) {
            throw new IllegalArgumentException("Course does not exist.");
        }

        return courseDAO.deleteCourse(courseId);
    }

    // VALIDATION
    private void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course details cannot be null.");
        }

        if (course.getCourseTitle() == null
                || course.getCourseTitle().isBlank()) {
            throw new IllegalArgumentException("Course title is required.");
        }

        if (course.getCourseTitle().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Course title cannot exceed 100 characters."
            );
        }

        if (course.getCourseCode() == null
                || course.getCourseCode().isBlank()) {
            throw new IllegalArgumentException("Course code is required.");
        }

        if (course.getCourseCode().trim().length() > 20) {
            throw new IllegalArgumentException(
                    "Course code cannot exceed 20 characters."
            );
        }

        if (course.getCreditHours() <= 0) {
            throw new IllegalArgumentException(
                    "Credit hours must be greater than zero."
            );
        }

        if (course.getDescription() != null
                && course.getDescription().length() > 1000) {
            throw new IllegalArgumentException(
                    "Course description cannot exceed 1000 characters."
            );
        }
    }

    private void validateCourseId(int courseId) {
        if (courseId <= 0) {
            throw new IllegalArgumentException(
                    "Course ID must be a positive number."
            );
        }
    }

    private boolean courseCodeExists(String courseCode, Integer currentCourseId) {
        String normalizedCode = courseCode.trim();

        return courseDAO.getAllCourses().stream()
                .anyMatch(existing ->
                        normalizedCode.equalsIgnoreCase(existing.getCourseCode())
                        && (currentCourseId == null
                            || existing.getCourseId() != currentCourseId)
                );
    }

    private void checkPermission(UserAccount user) {
        if (!AccessControl.hasPermission(user, Permission.MANAGE_COURSES)) {
            throw new SecurityException(
                    "You are not authorized to manage courses."
            );
        }
    }
}