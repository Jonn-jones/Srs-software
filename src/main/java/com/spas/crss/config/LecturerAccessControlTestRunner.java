package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

public class LecturerAccessControlTestRunner {

    public static void main(String[] args) {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        System.out.println("======================================");
        System.out.println("LECTURER ACCESS CONTROL TEST");
        System.out.println("======================================");

        UserAccount lecturer =
                userAccountDAO.getAccountByUsername("Phil");

        if (lecturer == null) {
            System.out.println("Lecturer account not found!");
            return;
        }

        System.out.println("Username: " + lecturer.getUsername());
        System.out.println("Role: " + lecturer.getRole());
        System.out.println("Lecturer ID: " + lecturer.getLecturerId());

        System.out.println("\n---------- PERMISSION TESTS ----------");

        testPermission(lecturer,
                Permission.VIEW_ASSIGNED_COURSES,
                true, "View assigned courses");

        testPermission(lecturer,
                Permission.VIEW_ENROLLED_STUDENTS,
                true, "View enrolled students");

        testPermission(lecturer,
                Permission.VIEW_TEACHING_TIMETABLE,
                true, "View teaching timetable");

        testPermission(lecturer,
                Permission.VIEW_AVAILABLE_COURSES,
                false, "View student available courses");

        testPermission(lecturer,
                Permission.REGISTER_COURSE,
                false, "Register for courses");

        testPermission(lecturer,
                Permission.MANAGE_COURSES,
                false, "Manage courses");

        testPermission(lecturer,
                Permission.MANAGE_LECTURERS,
                false, "Manage lecturers");

        System.out.println("\n======================================");
        System.out.println("LECTURER ACCESS TEST COMPLETED");
        System.out.println("======================================");
    }

    private static void testPermission(
            UserAccount user,
            Permission permission,
            boolean expected,
            String description) {

        boolean actual =
                AccessControl.hasPermission(user, permission);

        if (actual == expected) {
            System.out.println("[PASSED] " + description);
        } else {
            System.out.println("[FAILED] " + description);
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
        }
    }
}
