package com.spas.crss.config;

import com.spas.crss.security.UserRole;
import com.spas.crss.service.AuthenticationService;

import java.util.Scanner;

public class LecturerAccountTestRunner {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService authService =
                new AuthenticationService();

        System.out.println("====================================");
        System.out.println("LECTURER ACCOUNT CREATION");
        System.out.println("====================================");

        System.out.println("Lecturer ID: LEC007");
        System.out.println("Lecturer Name: Philemon Mwangi");

        System.out.print("Enter a username: ");
        String username = scanner.nextLine();

        System.out.print("Enter a password (minimum 8 characters): ");
        String password = scanner.nextLine();

        boolean created = authService.registerAccount(
                username,
                password,
                UserRole.LECTURER,
                "LEC007"
        );

        if (created) {
            System.out.println("\nLecturer account created successfully!");
        } else {
            System.out.println("\nLecturer account creation failed.");
        }

        scanner.close();
    }
}
