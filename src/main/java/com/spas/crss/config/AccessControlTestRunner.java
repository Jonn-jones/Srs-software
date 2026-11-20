package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

public class AccessControlTestRunner {

    public static void main(String[] args) {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        System.out.println("======================================");
        System.out.println("SPAS CRSS ACCESS CONTROL TEST");
        System.out.println("======================================");

        // Retrieve the existing student account
        UserAccount student =
                userAccountDAO.getAccountByUsername("Pixelpp");

        if (student == null) {
            System.out.println("Student account not found!");
            return;
        }

        System.out.println("Testing account: " + student.getUsername());
        System.out.println("Role: " + student.getRole());

        System.out.println("\n---------- STUDENT PERMISSION TESTS ----------");

        // Test 1: Student can view available courses
        testPermission(
                student,
                Permission.VIEW_AVAILABLE_COURSES,
                true,
                "View available courses"
        );

        // Test 2: Student can register for courses
        testPermission(
                student,
                Permission.REGISTER_COURSE,
                true,
                "Register for courses"
        );

        // Test 3: Student can withdraw from courses
        testPermission(
                student,
                Permission.WITHDRAW_COURSE,
                true,
                "Withdraw from courses"
        );

        // Test 4: Student can view own timetable
        testPermission(
                student,
                Permission.VIEW_OWN_TIMETABLE,
                true,
                "View own timetable"
        );

        // Test 5: Student cannot manage lecturers
        testPermission(
                student,
                Permission.MANAGE_LECTURERS,
                false,
                "Manage lecturers"
        );

        // Test 6: Student cannot manage courses
        testPermission(
                student,
                Permission.MANAGE_COURSES,
                false,
                "Manage courses"
        );

        // Test 7: Student cannot view assigned courses as a lecturer
        testPermission(
                student,
                Permission.VIEW_ASSIGNED_COURSES,
                false,
                "View lecturer assigned courses"
        );

        System.out.println("\n======================================");
        System.out.println("ACCESS CONTROL TEST COMPLETED");
        System.out.println("======================================");
    }

    private static void testPermission(
            UserAccount user,
            Permission permission,
            boolean expected,
            String description) {

        boolean actual = AccessControl.hasPermission(
                user,
                permission
        );

        if (actual == expected) {
            System.out.println("[PASSED] " + description);
        } else {
            System.out.println("[FAILED] " + description);
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
        }
    }
}