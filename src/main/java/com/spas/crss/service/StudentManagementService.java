package com.spas.crss.service;

import com.spas.crss.dao.StudentDAO;
import com.spas.crss.model.Student;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.sql.SQLException;
import java.util.List;

public class StudentManagementService {

    private final StudentDAO studentDAO;

    public StudentManagementService() {
        this.studentDAO = new StudentDAO();
    }

    // 1. REGISTER STUDENT
    public boolean addStudent(UserAccount user, Student student) {

        checkPermission(user);
        validateStudent(student);

        try {
            if (studentDAO.getStudentById(student.getStudentId()) != null) {
                throw new IllegalArgumentException(
                        "A student with this ID already exists."
                );
            }

            checkEmailAvailability(student.getEmail(), null);

            return studentDAO.addStudent(student);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to register student: " + e.getMessage(), e
            );
        }
    }

    // 2. GET STUDENT BY ID
    public Student getStudentById(UserAccount user, String studentId) {

        checkPermission(user);
        validateStudentId(studentId);

        try {
            return studentDAO.getStudentById(studentId);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve student: " + e.getMessage(), e
            );
        }
    }

    // 3. GET ALL STUDENTS
    public List<Student> getAllStudents(UserAccount user) {

        checkPermission(user);

        try {
            return studentDAO.getAllStudents();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve students: " + e.getMessage(), e
            );
        }
    }

    // 4. UPDATE STUDENT
    public boolean updateStudent(UserAccount user, Student student) {

        checkPermission(user);
        validateStudent(student);

        try {
            Student existingStudent =
                    studentDAO.getStudentById(student.getStudentId());

            if (existingStudent == null) {
                throw new IllegalArgumentException(
                        "Student record does not exist."
                );
            }

            checkEmailAvailability(
                    student.getEmail(),
                    student.getStudentId()
            );

            return studentDAO.updateStudent(student);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to update student: " + e.getMessage(), e
            );
        }
    }

    // 5. DELETE STUDENT
    public boolean deleteStudent(UserAccount user, String studentId) {

        checkPermission(user);
        validateStudentId(studentId);

        try {
            if (studentDAO.getStudentById(studentId) == null) {
                throw new IllegalArgumentException(
                        "Student record does not exist."
                );
            }

            return studentDAO.deleteStudent(studentId);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to delete student: " + e.getMessage(), e
            );
        }
    }

    // 6. VALIDATE STUDENT DETAILS
    private void validateStudent(Student student) {

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student details cannot be null."
            );
        }

        validateStudentId(student.getStudentId());

        if (student.getName() == null ||
                student.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Student name is required."
            );
        }

        if (student.getName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Student name cannot exceed 100 characters."
            );
        }

        if (student.getProgramme() == null ||
                student.getProgramme().isBlank()) {

            throw new IllegalArgumentException(
                    "Student programme is required."
            );
        }

        if (student.getProgramme().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Student programme cannot exceed 100 characters."
            );
        }

        if (student.getYear() <= 0) {
            throw new IllegalArgumentException(
                    "Year of study must be greater than zero."
            );
        }

        if (student.getEmail() != null &&
                !student.getEmail().isBlank()) {

            String email = student.getEmail().trim();

            if (email.length() > 100 ||
                    !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                throw new IllegalArgumentException(
                        "Invalid email address."
                );
            }
        }
    }

    // 7. VALIDATE STUDENT ID
    private void validateStudentId(String studentId) {

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        if (studentId.trim().length() > 20) {
            throw new IllegalArgumentException(
                    "Student ID cannot exceed 20 characters."
            );
        }
    }

    // 8. CHECK EMAIL AVAILABILITY
    private void checkEmailAvailability(
            String email,
            String currentStudentId) {

        if (email == null || email.isBlank()) {
            return;
        }

        try {
            boolean emailExists = studentDAO.getAllStudents().stream()
                    .anyMatch(existing ->
                            existing.getEmail() != null
                            && email.trim().equalsIgnoreCase(
                                    existing.getEmail().trim())
                            && !existing.getStudentId().equals(
                                    currentStudentId)
                    );

            if (emailExists) {
                throw new IllegalArgumentException(
                        "This email address is already assigned to another student."
                );
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check email availability: "
                            + e.getMessage(), e
            );
        }
    }

    // 9. CHECK PERMISSIONS
    private void checkPermission(UserAccount user) {

        if (!AccessControl.hasPermission(
                user,
                Permission.MANAGE_STUDENTS)) {

            throw new SecurityException(
                    "You are not authorized to manage student records."
            );
        }
    }
}
