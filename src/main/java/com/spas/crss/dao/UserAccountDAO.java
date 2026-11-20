package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserAccountDAO {

    // Create a new user account
    public boolean createAccount(UserAccount account) {

        String sql = """
                INSERT INTO user_account
                (username, password_hash, role, account_status,
                 student_id, lecturer_id, cod_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPasswordHash());
            statement.setString(3, account.getRole());
            statement.setString(4, account.getAccountStatus());
            statement.setString(5, account.getStudentId());
            statement.setString(6, account.getLecturerId());
            statement.setString(7, account.getCodId());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        account.setAccountId(keys.getInt(1));
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Account creation failed: " + e.getMessage());
        }

        return false;
    }

    // Find account using username
    public UserAccount getAccountByUsername(String username) {

        String sql = """
                SELECT * FROM user_account
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return extractAccount(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Account search failed: " + e.getMessage());
        }

        return null;
    }

    // Find account using account ID
    public UserAccount getAccountById(int accountId) {

        String sql = """
                SELECT * FROM user_account
                WHERE account_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return extractAccount(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Account retrieval failed: " + e.getMessage());
        }

        return null;
    }

    // Activate or deactivate an account
    public boolean updateAccountStatus(int accountId, String status) {

        String sql = """
                UPDATE user_account
                SET account_status = ?
                WHERE account_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, accountId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Status update failed: " + e.getMessage());
        }

        return false;
    }

    // Convert database record into UserAccount object
    private UserAccount extractAccount(ResultSet result)
            throws SQLException {

        UserAccount account = new UserAccount();

        account.setAccountId(result.getInt("account_id"));
        account.setUsername(result.getString("username"));
        account.setPasswordHash(result.getString("password_hash"));
        account.setRole(result.getString("role"));
        account.setAccountStatus(result.getString("account_status"));
        account.setStudentId(result.getString("student_id"));
        account.setLecturerId(result.getString("lecturer_id"));
        account.setCodId(result.getString("cod_id"));

        return account;
    }
}