
package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDAO {

    // =====================================================
    // 1. CREATE A NORMAL USER ACCOUNT
    // =====================================================

    public boolean createAccount(UserAccount account) {

        String sql = """
                INSERT INTO user_account
                (username, password_hash, role, account_status,
                 student_id, lecturer_id, cod_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

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


    // =====================================================
    // 2. CREATE THE FIRST SYSTEM ADMINISTRATOR
    // =====================================================

    public boolean createInitialSystemAdmin(UserAccount account) {

        if (account == null) {
            throw new IllegalArgumentException(
                    "System Admin account cannot be null."
            );
        }

        if (!"SYSTEM_ADMIN".equalsIgnoreCase(account.getRole())) {
            throw new IllegalArgumentException(
                    "Only SYSTEM_ADMIN accounts can use this method."
            );
        }

        if (account.getStudentId() != null
                || account.getLecturerId() != null
                || account.getCodId() != null) {

            throw new IllegalArgumentException(
                    "System Admin accounts must not be linked "
                    + "to academic profiles."
            );
        }

        final String lockName = "spas_crss_initial_admin";

        String countSql = """
                SELECT COUNT(*)
                FROM user_account
                WHERE role = 'SYSTEM_ADMIN'
                """;

        String insertSql = """
                INSERT INTO user_account
                (username, password_hash, role, account_status,
                 student_id, lecturer_id, cod_id)
                VALUES (?, ?, 'SYSTEM_ADMIN', 'ACTIVE', NULL, NULL, NULL)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            try (PreparedStatement lockStatement =
                         connection.prepareStatement("SELECT GET_LOCK(?, 10)")) {

                lockStatement.setString(1, lockName);

                try (ResultSet result = lockStatement.executeQuery()) {

                    if (!result.next() || result.getInt(1) != 1) {
                        throw new IllegalStateException(
                                "Could not acquire System Admin provisioning lock."
                        );
                    }
                }
            }

            try {

                connection.setAutoCommit(false);

                try (PreparedStatement countStatement =
                             connection.prepareStatement(countSql);
                     ResultSet result = countStatement.executeQuery()) {

                    if (result.next() && result.getInt(1) > 0) {
                        connection.rollback();

                        System.out.println(
                                "A System Admin account already exists."
                        );

                        return false;
                    }
                }

                try (PreparedStatement insertStatement =
                             connection.prepareStatement(
                                     insertSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    insertStatement.setString(1, account.getUsername());
                    insertStatement.setString(2, account.getPasswordHash());

                    int rowsInserted = insertStatement.executeUpdate();

                    if (rowsInserted > 0) {

                        try (ResultSet keys =
                                     insertStatement.getGeneratedKeys()) {

                            if (keys.next()) {
                                account.setAccountId(keys.getInt(1));
                            }
                        }

                        connection.commit();

                        System.out.println(
                                "Initial System Admin created successfully."
                        );

                        return true;
                    }
                }

                connection.rollback();
                return false;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();

                throw new IllegalStateException(
                        "System Admin provisioning failed: " + e.getMessage(),
                        e
                );

            } finally {

                connection.setAutoCommit(true);

                try (PreparedStatement releaseStatement =
                             connection.prepareStatement(
                                     "SELECT RELEASE_LOCK(?)")) {

                    releaseStatement.setString(1, lockName);

                    try (ResultSet ignored = releaseStatement.executeQuery()) {
                        // Lock release confirmed by database execution.
                    }
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Database error during System Admin provisioning: "
                    + e.getMessage(),
                    e
            );
        }
    }


    // =====================================================
    // 3. FIND ACCOUNT BY USERNAME
    // =====================================================

    public UserAccount getAccountByUsername(String username) {

        String sql = """
                SELECT *
                FROM user_account
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


    // =====================================================
    // 4. FIND ACCOUNT BY ID
    // =====================================================

    public UserAccount getAccountById(int accountId) {

        String sql = """
                SELECT *
                FROM user_account
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


    // =====================================================
    // 5. UPDATE ACCOUNT STATUS
    // =====================================================

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


    // =====================================================
    // 6. GET ALL USER ACCOUNTS
    // =====================================================

    public List<UserAccount> getAllAccounts() {

        List<UserAccount> accounts = new ArrayList<>();

        String sql = """
                SELECT *
                FROM user_account
                ORDER BY account_id ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                accounts.add(extractAccount(result));
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to retrieve user accounts: " + e.getMessage(),
                    e
            );
        }

        return accounts;
    }


    // =====================================================
    // 7. EXTRACT ACCOUNT FROM DATABASE RESULT
    // =====================================================

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