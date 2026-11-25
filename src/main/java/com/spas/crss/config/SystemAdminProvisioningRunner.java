package com.spas.crss.config;

import com.spas.crss.service.SystemAdminProvisioningService;

import java.io.Console;
import java.util.Scanner;

public class SystemAdminProvisioningRunner {

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" SPAS SYSTEM ADMIN PROVISIONING");
        System.out.println("======================================");

        Console console = System.console();

        if (console == null) {
            System.out.println(
                    "Run this program directly in a terminal "
                    + "that supports secure password input."
            );
            return;
        }

        String username = console.readLine("Enter Admin Username: ");

        char[] passwordChars =
                console.readPassword("Enter Admin Password: ");

        char[] confirmChars =
                console.readPassword("Confirm Admin Password: ");

        char[] bootstrapChars =
                console.readPassword("Enter Bootstrap Key: ");

        String password = new String(passwordChars);
        String confirmation = new String(confirmChars);
        String bootstrapKey = new String(bootstrapChars);

        if (!password.equals(confirmation)) {
            System.out.println("Passwords do not match.");
            return;
        }

        SystemAdminProvisioningService service =
                new SystemAdminProvisioningService();

        try {
            service.provisionInitialSystemAdmin(
                    username,
                    password,
                    bootstrapKey
            );
        } finally {
            java.util.Arrays.fill(passwordChars, '\0');
            java.util.Arrays.fill(confirmChars, '\0');
            java.util.Arrays.fill(bootstrapChars, '\0');
        }
    }
}
