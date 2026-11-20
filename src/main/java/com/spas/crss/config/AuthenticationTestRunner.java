package com.spas.crss.config;

import com.spas.crss.model.UserAccount;
import com.spas.crss.service.AuthenticationService;

import java.util.Scanner;

public class AuthenticationTestRunner {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService authService =
                new AuthenticationService();

        System.out.println("====================================");
        System.out.println("SPAS CRSS LOGIN TEST");
        System.out.println("====================================");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserAccount loggedInUser =
                authService.login(username, password);

        if (loggedInUser != null) {

            System.out.println("\nLOGIN TEST PASSED!");

            System.out.println("Username: "
                    + loggedInUser.getUsername());

            System.out.println("Role: "
                    + loggedInUser.getRole());

            System.out.println("Account ID: "
                    + loggedInUser.getAccountId());

        } else {

            System.out.println("\nLOGIN TEST FAILED!");
        }

        scanner.close();
    }
}