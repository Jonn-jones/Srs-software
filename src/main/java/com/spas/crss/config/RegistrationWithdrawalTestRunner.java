package com.spas.crss.config;

import com.spas.crss.model.UserAccount;
import com.spas.crss.service.AuthenticationService;
import com.spas.crss.service.RegistrationService;

import java.util.Scanner;

public class RegistrationWithdrawalTestRunner {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService authService =
                new AuthenticationService();

        RegistrationService registrationService =
                new RegistrationService();

        System.out.println("======================================");
        System.out.println("COURSE WITHDRAWAL TEST");
        System.out.println("======================================");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Authenticate student
        UserAccount user =
                authService.login(username, password);

        if (user == null) {
            System.out.println("Login failed.");
            scanner.close();
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.getUsername());

        // Display registration to withdraw
        System.out.println("\nRegistration ID: 1");
        System.out.println("Course ID: 1");

        System.out.print("\nConfirm withdrawal? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {

            boolean withdrawn =
                    registrationService.withdrawCourse(user, 1);

            if (withdrawn) {
                System.out.println("\nWITHDRAWAL TEST: PASSED");
            } else {
                System.out.println("\nWITHDRAWAL TEST: FAILED");
            }

        } else {
            System.out.println("Withdrawal cancelled.");
        }

        System.out.println("======================================");

        scanner.close();
    }
}
