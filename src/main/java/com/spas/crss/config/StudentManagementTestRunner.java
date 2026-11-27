package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.Student;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.StudentManagementService;

import java.util.List;

public class StudentManagementTestRunner {

    public static void main(String[] args) {

        StudentManagementService service =
                new StudentManagementService();

        UserAccountDAO accountDAO = new UserAccountDAO();

        // Retrieve actual database accounts
        UserAccount codUser =
                accountDAO.getAccountByUsername("Dismus");

        UserAccount studentUser =
                accountDAO.getAccountByUsername("Pixelpp");

        if (codUser == null || studentUser == null) {
            System.out.println("Required test accounts not found.");
            return;
        }

        String testId = "TESTSM001";
        String testEmail = "student.management.test@example.com";

        System.out.println("========================================");
        System.out.println("   STUDENT MANAGEMENT SERVICE TESTS");
        System.out.println("========================================");

        // Ensure the temporary test ID is not already in use
        if (service.getStudentById(codUser, testId) != null) {
            System.out.println(
                    "Test aborted: TESTSM001 already exists."
            );
            return;
        }

        // 1. REGISTER STUDENT
        System.out.println("\n1. REGISTER STUDENT");

        Student student = new Student(
                testId,
                "Test Student",
                "Bachelor of Data Science",
                2,
                testEmail
        );

        boolean added = service.addStudent(codUser, student);

        System.out.println(
                added ? "PASS: Student registered."
                      : "FAIL: Student registration failed."
        );

        // 2. RETRIEVE STUDENT BY ID
        System.out.println("\n2. RETRIEVE STUDENT");

        Student retrieved =
                service.getStudentById(codUser, testId);

        System.out.println(
                retrieved != null
                        ? "PASS: Student retrieved: "
                                + retrieved.getName()
                        : "FAIL: Student not found."
        );

        // 3. RETRIEVE ALL STUDENTS
        System.out.println("\n3. RETRIEVE ALL STUDENTS");

        List<Student> students =
                service.getAllStudents(codUser);

        System.out.println(
                "PASS: Retrieved " + students.size() + " students."
        );

        // 4. UPDATE STUDENT
        System.out.println("\n4. UPDATE STUDENT");

        student.setName("Updated Test Student");
        student.setYear(3);

        boolean updated = service.updateStudent(codUser, student);

        System.out.println(
                updated ? "PASS: Student updated."
                        : "FAIL: Student update failed."
        );

        // 5. TEST DUPLICATE STUDENT ID
        System.out.println("\n5. TEST DUPLICATE STUDENT ID");

        try {
            service.addStudent(codUser, student);

            System.out.println(
                    "FAIL: Duplicate student ID was accepted."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "PASS: Duplicate ID rejected."
            );
        }

        // 6. TEST DUPLICATE EMAIL
        System.out.println("\n6. TEST DUPLICATE EMAIL");

        Student duplicateEmailStudent = new Student(
                "TESTSM002",
                "Another Test Student",
                "Computer Science",
                1,
                testEmail
        );

        try {
            service.addStudent(codUser, duplicateEmailStudent);

            System.out.println(
                    "FAIL: Duplicate email was accepted."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "PASS: Duplicate email rejected."
            );
        }

        // 7. TEST UNAUTHORIZED ACCESS
        System.out.println("\n7. TEST UNAUTHORIZED ACCESS");

        try {
            Student unauthorizedStudent = new Student(
                    "TESTSM003",
                    "Unauthorized Test",
                    "Computer Science",
                    1,
                    "unauthorized.test@example.com"
            );

            service.addStudent(studentUser, unauthorizedStudent);

            System.out.println(
                    "FAIL: Student account was allowed to manage students."
            );

        } catch (SecurityException e) {

            System.out.println(
                    "PASS: Unauthorized access rejected."
            );
        }

        // 8. DELETE TEST STUDENT
        System.out.println("\n8. DELETE STUDENT");

        boolean deleted = service.deleteStudent(codUser, testId);

        System.out.println(
                deleted ? "PASS: Test student deleted."
                        : "FAIL: Student deletion failed."
        );

        // 9. VERIFY DELETION
        System.out.println("\n9. VERIFY DELETION");

        Student deletedStudent =
                service.getStudentById(codUser, testId);

        System.out.println(
                deletedStudent == null
                        ? "PASS: Student no longer exists."
                        : "FAIL: Student still exists."
        );

        System.out.println("\n========================================");
        System.out.println("   STUDENT MANAGEMENT TESTS COMPLETED");
        System.out.println("========================================");
    }
}
