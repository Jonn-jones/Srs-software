package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.Cod;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.CodManagementService;

public class CodManagementServiceTestRunner {

    public static void main(String[] args) {

        CodManagementService service = new CodManagementService();
        UserAccountDAO accountDAO = new UserAccountDAO();

        UserAccount codUser = accountDAO.getAccountByUsername("Dismus");
        UserAccount studentUser = accountDAO.getAccountByUsername("Pixelpp");

        String testId = "TEST" + System.currentTimeMillis();
        String testEmail = "test" + System.currentTimeMillis()
                + "@example.com";

        int passed = 0;
        int failed = 0;

        System.out.println("====================================");
        System.out.println(" CoD MANAGEMENT SERVICE TESTS");
        System.out.println("====================================");

        // 1. Add CoD
        try {
            boolean result = service.addCod(
                    codUser,
                    new Cod(testId, "Service Test Coordinator", testEmail)
            );

            if (result) {
                System.out.println("ADD CoD: PASSED");
                passed++;
            } else {
                System.out.println("ADD CoD: FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("ADD CoD: FAILED - " + e.getMessage());
            failed++;
        }

        // 2. Retrieve CoD
        try {
            Cod result = service.getCodById(codUser, testId);

            if (result != null) {
                System.out.println("RETRIEVE CoD: PASSED");
                passed++;
            } else {
                System.out.println("RETRIEVE CoD: FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("RETRIEVE CoD: FAILED - " + e.getMessage());
            failed++;
        }

        // 3. Update CoD
        try {
            boolean result = service.updateCod(
                    codUser,
                    new Cod(testId, "Updated Service Coordinator", testEmail)
            );

            if (result) {
                System.out.println("UPDATE CoD: PASSED");
                passed++;
            } else {
                System.out.println("UPDATE CoD: FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("UPDATE CoD: FAILED - " + e.getMessage());
            failed++;
        }

        // 4. Retrieve all CoDs
        try {
            boolean result = service.getAllCods(codUser).stream()
                    .anyMatch(cod -> cod.getCodId().equals(testId));

            if (result) {
                System.out.println("VIEW ALL CoDs: PASSED");
                passed++;
            } else {
                System.out.println("VIEW ALL CoDs: FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("VIEW ALL CoDs: FAILED - " + e.getMessage());
            failed++;
        }

        // 5. Reject invalid email
        try {
            service.addCod(
                    codUser,
                    new Cod(testId + "X", "Invalid Email Test", "invalid-email")
            );

            System.out.println("EMAIL VALIDATION: FAILED");
            failed++;

        } catch (IllegalArgumentException e) {
            System.out.println("EMAIL VALIDATION: PASSED");
            passed++;
        }

        // 6. Deny student access
        try {
            service.getAllCods(studentUser);

            System.out.println("ACCESS CONTROL: FAILED");
            failed++;

        } catch (SecurityException e) {
            System.out.println("ACCESS CONTROL: PASSED");
            passed++;
        }

        // 7. Delete test record
        try {
            boolean result = service.deleteCod(codUser, testId);

            if (result) {
                System.out.println("DELETE CoD: PASSED");
                passed++;
            } else {
                System.out.println("DELETE CoD: FAILED");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("DELETE CoD: FAILED - " + e.getMessage());
            failed++;
        }

        System.out.println("------------------------------------");
        System.out.println("PASSED: " + passed);
        System.out.println("FAILED: " + failed);
        System.out.println("------------------------------------");
    }
}
