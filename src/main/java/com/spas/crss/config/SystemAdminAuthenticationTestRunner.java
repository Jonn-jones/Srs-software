package com.spas.crss.config;

import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;
import com.spas.crss.service.AuthenticationService;

import java.io.Console;
import java.util.Arrays;

public class SystemAdminAuthenticationTestRunner {

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" SPAS SYSTEM ADMIN AUTHENTICATION TEST");
        System.out.println("======================================");

        Console console = System.console();

        if (console == null) {
            System.out.println(
                    "Please run this program in a terminal "
                    + "that supports secure password input."
            );
            return;
        }

        String username = console.readLine("Enter Admin Username: ");

        char[] passwordChars =
                console.readPassword("Enter Admin Password: ");

        String password = new String(passwordChars);

        try {

            AuthenticationService authenticationService =
                    new AuthenticationService();

            // 1. Test login
            UserAccount admin = authenticationService.login(
                    username,
                    password
            );

            if (admin == null) {
                System.out.println("AUTHENTICATION TEST FAILED.");
                return;
            }

            System.out.println("\n--- ACCOUNT VERIFICATION ---");

            System.out.println("Username: " + admin.getUsername());
            System.out.println("Role: " + admin.getRole());
            System.out.println("Status: " + admin.getAccountStatus());

            // 2. Test System Admin permissions
            System.out.println("\n--- PERMISSION TESTS ---");

            testPermission(
                    admin,
                    Permission.MANAGE_COD_PROFILES,
                    true
            );

            testPermission(
                    admin,
                    Permission.MANAGE_USER_ACCOUNTS,
                    true
            );

            testPermission(
                    admin,
                    Permission.VIEW_SYSTEM_OVERVIEW,
                    true
            );

            // 3. Verify that System Admin does not automatically
            // receive departmental management permissions.
            testPermission(
                    admin,
                    Permission.MANAGE_COURSES,
                    false
            );

            System.out.println("\n======================================");
            System.out.println(" SYSTEM ADMIN TEST COMPLETED");
            System.out.println("======================================");

        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    private static void testPermission(
            UserAccount admin,
            Permission permission,
            boolean expected) {

        boolean actual =
                AccessControl.hasPermission(admin, permission);

        String result = actual == expected ? "PASS" : "FAIL";

        System.out.println(
                permission + " : " + result
                + " (Expected: "
                + (expected ? "GRANTED" : "DENIED")
                + ", Actual: "
                + (actual ? "GRANTED" : "DENIED")
                + ")"
        );
    }
}
