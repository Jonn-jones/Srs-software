package com.spas.crss.config;

import com.spas.crss.model.UserAccount;
import com.spas.crss.model.Registration;
import com.spas.crss.service.AuthenticationService;
import com.spas.crss.service.RegistrationService;

import java.util.List;
import java.util.Scanner;

public class RegistrationViewTestRunner {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService authService =
                new AuthenticationService();

        RegistrationService registrationService =
                new RegistrationService();

        System.out.println("====================================");
        System.out.println("STUDENT REGISTRATION VIEW");
        System.out.println("====================================");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserAccount user =
                authService.login(username, password);

        if (user == null) {
            System.out.println("Authentication failed.");
            scanner.close();
            return;
        }

        List<Registration> registrations =
                registrationService.viewMyRegistrations(user);

        if (registrations.isEmpty()) {
            System.out.println("No registrations found.");
        } else {

            System.out.println("\nYOUR REGISTERED COURSES:");

            for (Registration registration : registrations) {
                System.out.println("--------------------------------");
                System.out.println("Registration ID: "
                        + registration.getRegistrationId());

                System.out.println("Course ID: "
                        + registration.getCourseId());

                System.out.println("Semester ID: "
                        + registration.getSemesterId());

                System.out.println("Registration Date: "
                        + registration.getRegistrationDate());

                System.out.println("Status: "
                        + registration.getRegistrationStatus());
            }
        }

        System.out.println("====================================");

        scanner.close();
    }
}