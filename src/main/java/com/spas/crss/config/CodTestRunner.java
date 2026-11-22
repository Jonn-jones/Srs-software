package com.spas.crss.config;

import com.spas.crss.dao.CodDAO;
import com.spas.crss.model.Cod;

public class CodTestRunner {

    public static void main(String[] args) {

        CodDAO codDAO = new CodDAO();

        String testId = "TEST" + System.currentTimeMillis();

        System.out.println("==================================");
        System.out.println("       CoD MANAGEMENT TEST");
        System.out.println("==================================");

        try {
            // 1. CREATE
            Cod newCod = new Cod(
                    testId,
                    "Test Coordinator",
                    "testcoordinator@example.com"
            );

            boolean added = codDAO.addCod(newCod);
            System.out.println("CREATE: " + (added ? "PASSED" : "FAILED"));

            // 2. READ
            Cod retrieved = codDAO.getCodById(testId);

            boolean found = retrieved != null;
            System.out.println("READ: " + (found ? "PASSED" : "FAILED"));

            // 3. UPDATE
            retrieved.setName("Updated Test Coordinator");

            boolean updated = codDAO.updateCod(retrieved);
            System.out.println("UPDATE: " + (updated ? "PASSED" : "FAILED"));

            // 4. VERIFY UPDATE
            Cod updatedCod = codDAO.getCodById(testId);

            boolean updateVerified = updatedCod != null
                    && updatedCod.getName().equals("Updated Test Coordinator");

            System.out.println("UPDATE VERIFICATION: "
                    + (updateVerified ? "PASSED" : "FAILED"));

            // 5. READ ALL
            boolean listed = codDAO.getAllCods().stream()
                    .anyMatch(cod -> cod.getCodId().equals(testId));

            System.out.println("READ ALL: " + (listed ? "PASSED" : "FAILED"));

            // 6. DELETE TEST RECORD
            boolean deleted = codDAO.deleteCod(testId);
            System.out.println("DELETE: " + (deleted ? "PASSED" : "FAILED"));

            // 7. VERIFY DELETION
            boolean deletionVerified = codDAO.getCodById(testId) == null;

            System.out.println("DELETE VERIFICATION: "
                    + (deletionVerified ? "PASSED" : "FAILED"));

            System.out.println("==================================");
            System.out.println("CoD MANAGEMENT TEST COMPLETED");
            System.out.println("==================================");

        } catch (Exception e) {
            System.out.println("TEST FAILED: " + e.getMessage());

            // Attempt to clean up the temporary test record.
            try {
                if (codDAO.getCodById(testId) != null) {
                    codDAO.deleteCod(testId);
                }
            } catch (Exception cleanupError) {
                System.out.println("Cleanup failed: " + cleanupError.getMessage());
            }
        }
    }
}
