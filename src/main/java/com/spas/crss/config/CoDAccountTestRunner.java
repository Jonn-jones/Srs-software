package com.spas.crss.config;

import com.spas.crss.security.UserRole;
import com.spas.crss.service.AuthenticationService;

import java.util.Scanner;

public class CoDAccountTestRunner {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService authService =
                new AuthenticationService();

        System.out.println("====================================");
        System.out.println("CoD ACCOUNT CREATION");
        System.out.println("====================================");

        System.out.println("Coordinator ID: TESTCOD001");
        System.out.println("Coordinator Name: Test Coordinator");

        System.out.print("Enter a username: ");
        String username = scanner.nextLine();

        System.out.print("Enter a password (minimum 8 characters): ");
        String password = scanner.nextLine();

        boolean created = authService.registerAccount(
                username,
                password,
                UserRole.COD,
                "TESTCOD001"
        );

        if (created) {
            System.out.println("\nCoD account created successfully!");
        } else {
            System.out.println("\nCoD account creation failed.");
        }

        scanner.close();
    }
}