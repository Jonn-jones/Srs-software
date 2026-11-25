package com.spas.crss.service;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.PasswordUtil;
import com.spas.crss.security.UserRole;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SystemAdminProvisioningService {

    private final UserAccountDAO userAccountDAO;

    public SystemAdminProvisioningService() {
        this.userAccountDAO = new UserAccountDAO();
    }

    // =====================================================
    // PROVISION THE FIRST SYSTEM ADMINISTRATOR
    // =====================================================

    public boolean provisionInitialSystemAdmin(
            String username,
            String password,
            String suppliedBootstrapKey) {

        // 1. Validate username
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        if (username.trim().length() > 50) {
            System.out.println("Username cannot exceed 50 characters.");
            return false;
        }

        // 2. Validate password
        if (password == null || password.length() < 12) {
            System.out.println(
                    "System Admin password must contain at least 12 characters."
            );
            return false;
        }

        // 3. Retrieve the bootstrap key from environment variables
        String configuredBootstrapKey =
                System.getenv("SPAS_ADMIN_BOOTSTRAP_KEY");

        if (configuredBootstrapKey == null
                || configuredBootstrapKey.isBlank()) {

            System.out.println(
                    "System Admin provisioning is not configured."
            );

            return false;
        }

        // 4. Validate the supplied bootstrap key
        if (suppliedBootstrapKey == null
                || suppliedBootstrapKey.isBlank()) {

            System.out.println("Invalid bootstrap credentials.");
            return false;
        }

        boolean validKey = MessageDigest.isEqual(
                configuredBootstrapKey.getBytes(StandardCharsets.UTF_8),
                suppliedBootstrapKey.getBytes(StandardCharsets.UTF_8)
        );

        if (!validKey) {
            System.out.println("Invalid bootstrap credentials.");
            return false;
        }

        // 5. Hash the administrator's password
        String hashedPassword =
                PasswordUtil.hashPassword(password);

        // 6. Construct the System Admin account
        UserAccount adminAccount = new UserAccount(
                username.trim(),
                hashedPassword,
                UserRole.SYSTEM_ADMIN.name(),
                "ACTIVE",
                null,
                null,
                null
        );

        // 7. Save using the protected DAO provisioning method
        boolean created =
                userAccountDAO.createInitialSystemAdmin(adminAccount);

        if (created) {
            System.out.println(
                    "========================================"
            );
            System.out.println(
                    "SYSTEM ADMIN PROVISIONED SUCCESSFULLY"
            );
            System.out.println(
                    "========================================"
            );
            System.out.println(
                    "Username: " + adminAccount.getUsername()
            );
            System.out.println(
                    "Account ID: " + adminAccount.getAccountId()
            );

            return true;
        }

        System.out.println(
                "System Admin provisioning was unsuccessful."
        );

        return false;
    }
}
