package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.Lecturer;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.LecturerManagementService;

import java.util.List;

public class LecturerManagementTestRunner {

    public static void main(String[] args) {

        LecturerManagementService service =
                new LecturerManagementService();

        UserAccountDAO accountDAO = new UserAccountDAO();

        UserAccount codUser =
                accountDAO.getAccountByUsername("Dismus");

        UserAccount studentUser =
                accountDAO.getAccountByUsername("Pixelpp");

        if (codUser == null || studentUser == null) {
            System.out.println("Required test accounts not found.");
            return;
        }

        String testId = "TESTL001";
        String testEmail = "lecturer.test@example.com";

        System.out.println("========================================");
        System.out.println("   LECTURER MANAGEMENT SERVICE TESTS");
        System.out.println("========================================");

        if (service.getLecturerById(codUser, testId) != null) {
            System.out.println(
                    "Test aborted: TESTL001 already exists."
            );
            return;
        }

        // 1. ADD LECTURER
        System.out.println("\n1. ADD LECTURER");

        Lecturer lecturer = new Lecturer(
                testId,
                "Test Lecturer",
                "Masters",
                testEmail,
                "Computing"
        );

        boolean added = service.addLecturer(codUser, lecturer);

        System.out.println(
                added ? "PASS: Lecturer added."
                      : "FAIL: Lecturer could not be added."
        );

        // 2. RETRIEVE LECTURER BY ID
        System.out.println("\n2. RETRIEVE LECTURER");

        Lecturer retrieved = service.getLecturerById(codUser, testId);

        System.out.println(
                retrieved != null
                        ? "PASS: Lecturer retrieved: " + retrieved.getName()
                        : "FAIL: Lecturer not found."
        );

        // 3. RETRIEVE ALL LECTURERS
        System.out.println("\n3. RETRIEVE ALL LECTURERS");

        List<Lecturer> lecturers = service.getAllLecturers(codUser);

        System.out.println(
                "PASS: Retrieved " + lecturers.size() + " lecturers."
        );

        // 4. UPDATE LECTURER
        System.out.println("\n4. UPDATE LECTURER");

        lecturer.setName("Updated Test Lecturer");
        lecturer.setQualification("PhD");

        boolean updated = service.updateLecturer(codUser, lecturer);

        System.out.println(
                updated ? "PASS: Lecturer updated."
                        : "FAIL: Lecturer update failed."
        );

        // 5. TEST DUPLICATE LECTURER ID
        System.out.println("\n5. TEST DUPLICATE LECTURER ID");

        try {
            service.addLecturer(codUser, lecturer);
            System.out.println("FAIL: Duplicate lecturer ID was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Duplicate lecturer ID rejected.");
        }

        // 6. TEST DUPLICATE EMAIL
        System.out.println("\n6. TEST DUPLICATE EMAIL");

        Lecturer duplicateEmailLecturer = new Lecturer(
                "TESTL002",
                "Another Test Lecturer",
                "Degree",
                testEmail,
                "Computing"
        );

        try {
            service.addLecturer(codUser, duplicateEmailLecturer);
            System.out.println("FAIL: Duplicate email was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Duplicate email rejected.");
        }

        // 7. TEST UNAUTHORIZED ACCESS
        System.out.println("\n7. TEST UNAUTHORIZED ACCESS");

        try {
            Lecturer unauthorizedLecturer = new Lecturer(
                    "TESTL003",
                    "Unauthorized Lecturer",
                    "Degree",
                    "unauthorized.lecturer@example.com",
                    "Computing"
            );

            service.addLecturer(studentUser, unauthorizedLecturer);

            System.out.println(
                    "FAIL: Student account was allowed to manage lecturers."
            );

        } catch (SecurityException e) {
            System.out.println("PASS: Unauthorized access rejected.");
        }

        // 8. DELETE TEST LECTURER
        System.out.println("\n8. DELETE LECTURER");

        boolean deleted = service.deleteLecturer(codUser, testId);

        System.out.println(
                deleted ? "PASS: Test lecturer deleted."
                        : "FAIL: Lecturer deletion failed."
        );

        // 9. VERIFY DELETION
        System.out.println("\n9. VERIFY DELETION");

        Lecturer deletedLecturer =
                service.getLecturerById(codUser, testId);

        System.out.println(
                deletedLecturer == null
                        ? "PASS: Lecturer no longer exists."
                        : "FAIL: Lecturer still exists."
        );

        System.out.println("\n========================================");
        System.out.println("   LECTURER MANAGEMENT TESTS COMPLETED");
        System.out.println("========================================");
    }
}
