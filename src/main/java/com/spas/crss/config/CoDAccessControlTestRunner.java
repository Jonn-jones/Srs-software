package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

public class CoDAccessControlTestRunner {

    public static void main(String[] args) {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        System.out.println("======================================");
        System.out.println("CoD ACCESS CONTROL TEST");
        System.out.println("======================================");

        UserAccount cod =
                userAccountDAO.getAccountByUsername("Dismus");

        if (cod == null) {
            System.out.println("CoD account not found!");
            return;
        }

        System.out.println("Username: " + cod.getUsername());
        System.out.println("Role: " + cod.getRole());
        System.out.println("CoD ID: " + cod.getCodId());

        System.out.println("\n---------- ADMIN PERMISSION TESTS ----------");

        testPermission(cod, Permission.MANAGE_STUDENTS, true,
                "Manage students");

        testPermission(cod, Permission.MANAGE_LECTURERS, true,
                "Manage lecturers");

        testPermission(cod, Permission.MANAGE_COURSES, true,
                "Manage courses");

        testPermission(cod, Permission.MANAGE_SEMESTERS, true,
                "Manage semesters");

        testPermission(cod, Permission.MANAGE_SPECIALIZATIONS, true,
                "Manage specializations");

        testPermission(cod, Permission.MANAGE_PREREQUISITES, true,
                "Manage prerequisites");

        testPermission(cod, Permission.MANAGE_COURSE_ASSIGNMENTS, true,
                "Manage course assignments");

        testPermission(cod, Permission.MANAGE_VENUES, true,
                "Manage venues");

        testPermission(cod, Permission.MANAGE_SCHEDULES, true,
                "Manage schedules");

        testPermission(cod, Permission.VIEW_ALL_REGISTRATIONS, true,
                "View all registrations");

        System.out.println("\n======================================");
        System.out.println("CoD ACCESS TEST COMPLETED");
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
