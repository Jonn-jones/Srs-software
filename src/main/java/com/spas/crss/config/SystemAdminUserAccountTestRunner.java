
package com.spas.crss.config;

import com.spas.crss.model.UserAccount;
import com.spas.crss.service.AuthenticationService;
import com.spas.crss.service.SystemAdminUserAccountService;

import java.io.Console;
import java.util.Arrays;
import java.util.List;

public class SystemAdminUserAccountTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println(" SPAS USER ACCOUNT MANAGEMENT TESTS");
        System.out.println("==========================================");

        Console console = System.console();

        if (console == null) {
            System.out.println(
                    "Run this program in a terminal supporting secure input."
            );
            return;
        }

        String username = console.readLine("Admin Username: ");
        char[] passwordChars =
                console.readPassword("Admin Password: ");

        String password = new String(passwordChars);

        try {
            AuthenticationService authenticationService =
                    new AuthenticationService();

            UserAccount admin =
                    authenticationService.login(username, password);

            if (admin == null) {
                System.out.println("Admin authentication failed.");
                return;
            }

            SystemAdminUserAccountService service =
                    new SystemAdminUserAccountService();

            System.out.println("\n--- ACCOUNT MANAGEMENT TESTS ---");

            // TEST 1: View all accounts
            try {
                List<UserAccount> accounts =
                        service.viewAllAccounts(admin);

                check(
                        "View all user accounts",
                        accounts != null
                );

                System.out.println("Accounts found: " + accounts.size());

                for (UserAccount account : accounts) {
                    System.out.println(
                            account.getAccountId()
                            + " | "
                            + account.getUsername()
                            + " | "
                            + account.getRole()
                            + " | "
                            + account.getAccountStatus()
                    );
                }

                boolean hashesHidden = accounts.stream()
                        .allMatch(account ->
                                account.getPasswordHash() == null
                        );

                check(
                        "Password hashes hidden from account listings",
                        hashesHidden
                );

            } catch (Exception e) {
                check("View all user accounts", false);
                System.out.println(e.getMessage());
            }

            // TEST 2: Search by account ID
            try {
                UserAccount found =
                        service.searchAccountById(
                                admin,
                                admin.getAccountId()
                        );

                check(
                        "Search account by ID",
                        found != null
                                && found.getAccountId()
                                == admin.getAccountId()
                );

            } catch (Exception e) {
                check("Search account by ID", false);
                System.out.println(e.getMessage());
            }

            // TEST 3: Search by username
            try {
                UserAccount found =
                        service.searchAccountByUsername(
                                admin,
                                admin.getUsername()
                        );

                check(
                        "Search account by username",
                        found != null
                                && found.getUsername().equalsIgnoreCase(
                                        admin.getUsername()
                                )
                );

            } catch (Exception e) {
                check("Search account by username", false);
                System.out.println(e.getMessage());
            }

            // TEST 4: Filter by role
            try {
                List<UserAccount> admins =
                        service.getAccountsByRole(
                                admin,
                                "SYSTEM_ADMIN"
                        );

                check(
                        "Filter accounts by role",
                        admins != null
                                && admins.stream().anyMatch(account ->
                                        account.getAccountId()
                                        == admin.getAccountId()
                                )
                );

            } catch (Exception e) {
                check("Filter accounts by role", false);
                System.out.println(e.getMessage());
            }

            // TEST 5: Filter by status
            try {
                List<UserAccount> activeAccounts =
                        service.getAccountsByStatus(
                                admin,
                                "ACTIVE"
                        );

                check(
                        "Filter accounts by status",
                        activeAccounts != null
                                && activeAccounts.stream().anyMatch(account ->
                                        account.getAccountId()
                                        == admin.getAccountId()
                                )
                );

            } catch (Exception e) {
                check("Filter accounts by status", false);
                System.out.println(e.getMessage());
            }

            // TEST 6: Prevent self-deactivation
            try {
                boolean result =
                        service.deactivateAccount(
                                admin,
                                admin.getAccountId()
                        );

                check(
                        "Prevent administrator self-deactivation",
                        !result
                );

            } catch (Exception e) {
                check(
                        "Prevent administrator self-deactivation",
                        false
                );

                System.out.println(e.getMessage());
            }

            System.out.println("\n==========================================");
            System.out.println(" USER ACCOUNT MANAGEMENT TEST RESULTS");
            System.out.println("==========================================");
            System.out.println("PASSED: " + passed);
            System.out.println("FAILED: " + failed);
            System.out.println("==========================================");

        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    private static void check(String testName, boolean condition) {

        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName);
        }
    }
}

