package com.spas.crss.service;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.PasswordUtil;
import com.spas.crss.security.UserRole;

public class AuthenticationService {

    private final UserAccountDAO userAccountDAO;

    public AuthenticationService() {
        this.userAccountDAO = new UserAccountDAO();
    }

    // Create a new user account
    public boolean registerAccount(
            String username,
            String password,
            UserRole role,
            String linkedId) {

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        // Validate password
        if (password == null || password.length() < 8) {
            System.out.println("Password must contain at least 8 characters.");
            return false;
        }

        // Validate role
        if (role == null) {
            System.out.println("Invalid user role.");
            return false;
        }

        // Prevent public System Admin registration
        if (role == UserRole.SYSTEM_ADMIN) {
            System.out.println(
                    "System Admin accounts must be created through "
                    + "the secure provisioning process."
            );
            return false;
        }

        // Validate linked record ID
        if (linkedId == null || linkedId.trim().isEmpty()) {
            System.out.println("A linked user ID is required.");
            return false;
        }

        // Check whether username already exists
        if (userAccountDAO.getAccountByUsername(username.trim()) != null) {
            System.out.println("Username already exists.");
            return false;
        }

        // Hash password before saving
        String hashedPassword = PasswordUtil.hashPassword(password);

        String studentId = null;
        String lecturerId = null;
        String codId = null;

        // Associate account with the correct role
        switch (role) {

            case STUDENT:
                studentId = linkedId.trim();
                break;

            case LECTURER:
                lecturerId = linkedId.trim();
                break;

            case COD:
                codId = linkedId.trim();
                break;

            case SYSTEM_ADMIN:
                // Already blocked above.
                return false;
        }

        UserAccount account = new UserAccount(
                username.trim(),
                hashedPassword,
                role.name(),
                "ACTIVE",
                studentId,
                lecturerId,
                codId
        );

        boolean created = userAccountDAO.createAccount(account);

        if (created) {
            System.out.println("Account created successfully!");
            System.out.println("Username: " + username);
            System.out.println("Role: " + role);
            return true;
        }

        System.out.println("Account creation failed.");
        return false;
    }

    // Authenticate a user
    public UserAccount login(String username, String password) {

        if (username == null || password == null) {
            System.out.println("Username and password are required.");
            return null;
        }

        UserAccount account =
                userAccountDAO.getAccountByUsername(username.trim());

        if (account == null) {
            System.out.println("Invalid username or password.");
            return null;
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getAccountStatus())) {
            System.out.println("Your account is inactive.");
            return null;
        }

        boolean passwordCorrect = PasswordUtil.verifyPassword(
                password,
                account.getPasswordHash()
        );

        if (!passwordCorrect) {
            System.out.println("Invalid username or password.");
            return null;
        }

        try {
            UserRole.valueOf(account.getRole().toUpperCase());

        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Your account has an invalid role.");
            return null;
        }

        System.out.println("Login successful!");
        System.out.println("Welcome, " + account.getUsername());
        System.out.println("Role: " + account.getRole());

        // Never expose the stored password hash
        account.setPasswordHash(null);

        return account;
    }
}