package com.spas.crss.config;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.mindrot.jbcrypt.BCrypt;

public class ResetStudentPasswordRunner {

    public static void main(String[] args) {

        Console console = System.console();

        if (console == null) {
            System.out.println("Please run this utility directly from a terminal.");
            return;
        }

        char[] passwordChars = console.readPassword("Enter new admin password: ");
        char[] confirmChars = console.readPassword("Confirm new admin password: ");

        String password = new String(passwordChars);
        String confirmation = new String(confirmChars);

        if (password.length() < 12) {
            System.out.println("Password must contain at least 12 characters.");
            return;
        }

        if (!password.equals(confirmation)) {
            System.out.println("Passwords do not match.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        String sql = """
                UPDATE user_account
                SET password_hash = ?
                WHERE username = ?
                AND account_status = 'ACTIVE'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashedPassword);
            statement.setString(2, "AdminSpas");

            int updated = statement.executeUpdate();

            if (updated == 1) {
                System.out.println("SUCCESS: AdminSpas password reset successfully.");
            } else {
                System.out.println("FAILED: No active AdminSpas account was updated.");
            }

        } catch (Exception e) {
            System.out.println("Password reset failed: " + e.getMessage());
        }
    }
}