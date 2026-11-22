package com.spas.crss.config;

import com.spas.crss.model.UserAccount;
import com.spas.crss.service.AuthenticationService;
import com.spas.crss.service.RegistrationService;

import java.util.Scanner;

public class RegistrationTestRunner {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService authenticationService =
                new AuthenticationService();

        RegistrationService registrationService =
                new RegistrationService();

        System.out.println("======================================");
        System.out.println("SPAS COURSE REGISTRATION TEST");
        System.out.println("======================================");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Authenticate student
        UserAccount user =
                authenticationService.login(username, password);

        if (user == null) {
            System.out.println("Login failed. Registration cancelled.");
            return;
        }

        System.out.println("\nLogin successful!");

        System.out.println("Welcome, " + user.getUsername());

        // Register for Introduction to Programming
        System.out.println("\nRegistering for Course ID: 2");

        boolean registered =
                registrationService.registerCourse(user, 2);

        if (registered) {
            System.out.println("\nTEST RESULT: PASSED");
        } else {
            System.out.println("\nTEST RESULT: REGISTRATION NOT COMPLETED");
        }

        System.out.println("======================================");

        scanner.close();
    }
}
