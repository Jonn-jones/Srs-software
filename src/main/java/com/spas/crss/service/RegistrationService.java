package com.spas.crss.service;

import com.spas.crss.dao.RegistrationDAO;
import com.spas.crss.dao.AcademicSemesterDAO;
import com.spas.crss.dao.CourseDAO;

import com.spas.crss.model.Registration;
import com.spas.crss.model.UserAccount;
import com.spas.crss.model.AcademicSemester;
import com.spas.crss.model.Course;

import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.time.LocalDate;
import java.util.List;

public class RegistrationService {

    private final RegistrationDAO registrationDAO;
    private final AcademicSemesterDAO semesterDAO;
    private final CourseDAO courseDAO;

    public RegistrationService() {
        this.registrationDAO = new RegistrationDAO();
        this.semesterDAO = new AcademicSemesterDAO();
        this.courseDAO = new CourseDAO();
    }

    // 1. REGISTER OR RE-REGISTER A STUDENT FOR A COURSE
    public boolean registerCourse(UserAccount user, int courseId) {

        // Check user permissions
        if (!AccessControl.hasPermission(
                user, Permission.REGISTER_COURSE)) {

            System.out.println(
                    "Access denied: You cannot register courses.");

            return false;
        }

        // Validate student account
        if (user.getStudentId() == null) {

            System.out.println("Invalid student account.");

            return false;
        }

        // Retrieve the active semester
        AcademicSemester semester = semesterDAO.getActiveSemester();

        if (semester == null) {

            System.out.println(
                    "Registration unavailable: No active semester.");

            return false;
        }

        // Validate registration period
        LocalDate today = LocalDate.now();

        LocalDate startDate = semester.getStartDate().toLocalDate();
        LocalDate endDate = semester.getEndDate().toLocalDate();

        if (today.isBefore(startDate) || today.isAfter(endDate)) {

            System.out.println(
                    "Registration is currently outside the semester dates.");

            return false;
        }

        String studentId = user.getStudentId();
        int semesterId = semester.getSemesterId();

        // Validate whether the selected course exists
        Course course = courseDAO.getCourseById(courseId);

        if (course == null) {

            System.out.println(
                    "Registration failed: The selected course does not exist.");

            return false;
        }

        // Find an existing registration
        Registration existingRegistration =
                registrationDAO.getRegistrationByStudentCourseSemester(
                        studentId, courseId, semesterId);

        // Handle an existing registration
        if (existingRegistration != null) {

            String status =
                    existingRegistration.getRegistrationStatus();

            // Student is already registered
            if ("ACTIVE".equalsIgnoreCase(status)) {

                System.out.println(
                        "You are already registered for this course.");

                return false;
            }

            // Reactivate a previously withdrawn registration
            if ("WITHDRAWN".equalsIgnoreCase(status)) {

                boolean reactivated =
                        registrationDAO.reactivateRegistration(
                                studentId,
                                courseId,
                                semesterId,
                                today
                        );

                if (reactivated) {

                    System.out.println(
                            "Course re-registration successful!");

                    System.out.println(
                            "Course: " + course.getCourseTitle());

                    System.out.println(
                            "Previous Registration ID: "
                                    + existingRegistration.getRegistrationId());

                    System.out.println(
                            "Registration Date: " + today);

                    return true;
                }

                System.out.println(
                        "Course re-registration failed.");

                return false;
            }

            // Handle unexpected registration statuses
            System.out.println(
                    "Registration cannot proceed because its status is: "
                            + status);

            return false;
        }

        // No previous registration: create a new registration
        Registration registration = new Registration(
                studentId,
                courseId,
                semesterId,
                today,
                "ACTIVE"
        );

        boolean registered =
                registrationDAO.createRegistration(registration);

        if (registered) {

            System.out.println(
                    "Course registration successful!");

            System.out.println(
                    "Registration ID: "
                            + registration.getRegistrationId());

            System.out.println(
                    "Course: " + course.getCourseTitle());

            System.out.println(
                    "Course Code: " + course.getCourseCode());

            System.out.println(
                    "Academic Year: " + semester.getAcademicYear());

            System.out.println(
                    "Semester: " + semester.getSemesterName());

            return true;
        }

        System.out.println("Course registration failed.");

        return false;
    }

    // 2. VIEW A STUDENT'S REGISTRATIONS
    public List<Registration> viewMyRegistrations(UserAccount user) {

        if (!AccessControl.hasPermission(
                user, Permission.VIEW_OWN_REGISTRATIONS)) {

            System.out.println("Access denied.");

            return List.of();
        }

        if (user.getStudentId() == null) {

            System.out.println("Invalid student account.");

            return List.of();
        }

        return registrationDAO.getRegistrationsByStudent(
                user.getStudentId());
    }

    // 3. WITHDRAW FROM A COURSE
    public boolean withdrawCourse(
            UserAccount user, int registrationId) {

        if (!AccessControl.hasPermission(
                user, Permission.WITHDRAW_COURSE)) {

            System.out.println(
                    "Access denied: You cannot withdraw courses.");

            return false;
        }

        if (user.getStudentId() == null) {

            System.out.println("Invalid student account.");

            return false;
        }

        // Retrieve registration
        Registration registration =
                registrationDAO.getRegistrationById(registrationId);

        if (registration == null) {

            System.out.println("Registration not found.");

            return false;
        }

        // Verify registration ownership
        if (!registration.getStudentId().equals(user.getStudentId())) {

            System.out.println(
                    "Access denied: This registration belongs to another student.");

            return false;
        }

        // Perform withdrawal
        boolean withdrawn =
                registrationDAO.withdrawRegistration(registrationId);

        if (withdrawn) {

            System.out.println(
                    "Course withdrawal successful!");

            return true;
        }

        System.out.println("Withdrawal failed.");

        return false;
    }
}