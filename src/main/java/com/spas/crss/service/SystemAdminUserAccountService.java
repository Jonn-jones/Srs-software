package com.spas.crss.service;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.ArrayList;
import java.util.List;

public class SystemAdminUserAccountService {

    private final UserAccountDAO userAccountDAO;

    public SystemAdminUserAccountService() {
        this.userAccountDAO = new UserAccountDAO();
    }

    // =====================================================
    // 1. VERIFY SYSTEM ADMIN PERMISSION
    // =====================================================

    private boolean isAuthorized(UserAccount admin) {

        return AccessControl.hasPermission(
                admin,
                Permission.MANAGE_USER_ACCOUNTS
        );
    }


    // =====================================================
    // 2. VIEW ALL USER ACCOUNTS
    // =====================================================

    public List<UserAccount> viewAllAccounts(UserAccount admin) {

        if (!isAuthorized(admin)) {
            throw new SecurityException(
                    "Access denied. System Admin permission required."
            );
        }

        List<UserAccount> accounts =
                userAccountDAO.getAllAccounts();

        // Never expose password hashes to callers.
        for (UserAccount account : accounts) {
            account.setPasswordHash(null);
        }

        return accounts;
    }


    // =====================================================
    // 3. SEARCH ACCOUNT BY ID
    // =====================================================

    public UserAccount searchAccountById(
            UserAccount admin,
            int accountId) {

        if (!isAuthorized(admin)) {
            throw new SecurityException("Access denied.");
        }

        if (accountId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid account ID."
            );
        }

        UserAccount account =
                userAccountDAO.getAccountById(accountId);

        if (account != null) {
            account.setPasswordHash(null);
        }

        return account;
    }


    // =====================================================
    // 4. SEARCH ACCOUNT BY USERNAME
    // =====================================================

    public UserAccount searchAccountByUsername(
            UserAccount admin,
            String username) {

        if (!isAuthorized(admin)) {
            throw new SecurityException("Access denied.");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        UserAccount account =
                userAccountDAO.getAccountByUsername(username.trim());

        if (account != null) {
            account.setPasswordHash(null);
        }

        return account;
    }


    // =====================================================
    // 5. ACTIVATE AN ACCOUNT
    // =====================================================

    public boolean activateAccount(
            UserAccount admin,
            int accountId) {

        if (!isAuthorized(admin)) {
            throw new SecurityException("Access denied.");
        }

        UserAccount target =
                userAccountDAO.getAccountById(accountId);

        if (target == null) {
            System.out.println("Account not found.");
            return false;
        }

        if ("ACTIVE".equalsIgnoreCase(target.getAccountStatus())) {
            System.out.println("Account is already active.");
            return false;
        }

        boolean updated =
                userAccountDAO.updateAccountStatus(
                        accountId,
                        "ACTIVE"
                );

        if (updated) {
            System.out.println("Account activated successfully.");
        }

        return updated;
    }


    // =====================================================
    // 6. DEACTIVATE AN ACCOUNT
    // =====================================================

    public boolean deactivateAccount(
            UserAccount admin,
            int accountId) {

        if (!isAuthorized(admin)) {
            throw new SecurityException("Access denied.");
        }

        UserAccount target =
                userAccountDAO.getAccountById(accountId);

        if (target == null) {
            System.out.println("Account not found.");
            return false;
        }

        if ("INACTIVE".equalsIgnoreCase(target.getAccountStatus())) {
            System.out.println("Account is already inactive.");
            return false;
        }

        // Prevent an administrator from deactivating themselves.
        if (admin.getAccountId() == accountId) {
            System.out.println(
                    "You cannot deactivate your own account."
            );
            return false;
        }

        // Protect the last active System Admin account.
        if ("SYSTEM_ADMIN".equalsIgnoreCase(target.getRole())
                && "ACTIVE".equalsIgnoreCase(target.getAccountStatus())) {

            List<UserAccount> accounts =
                    userAccountDAO.getAllAccounts();

            long activeAdmins = accounts.stream()
                    .filter(account ->
                            "SYSTEM_ADMIN".equalsIgnoreCase(
                                    account.getRole()
                            )
                    )
                    .filter(account ->
                            "ACTIVE".equalsIgnoreCase(
                                    account.getAccountStatus()
                            )
                    )
                    .count();

            if (activeAdmins <= 1) {
                System.out.println(
                        "Cannot deactivate the last active System Admin."
                );
                return false;
            }
        }

        boolean updated =
                userAccountDAO.updateAccountStatus(
                        accountId,
                        "INACTIVE"
                );

        if (updated) {
            System.out.println("Account deactivated successfully.");
        }

        return updated;
    }


    // =====================================================
    // 7. SEARCH ACCOUNTS BY ROLE
    // =====================================================

    public List<UserAccount> getAccountsByRole(
            UserAccount admin,
            String role) {

        if (!isAuthorized(admin)) {
            throw new SecurityException("Access denied.");
        }

        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Role cannot be empty."
            );
        }

        List<UserAccount> allAccounts =
                userAccountDAO.getAllAccounts();

        List<UserAccount> filteredAccounts =
                new ArrayList<>();

        for (UserAccount account : allAccounts) {

            if (role.trim().equalsIgnoreCase(account.getRole())) {

                account.setPasswordHash(null);
                filteredAccounts.add(account);
            }
        }

        return filteredAccounts;
    }


    // =====================================================
    // 8. SEARCH ACCOUNTS BY STATUS
    // =====================================================

    public List<UserAccount> getAccountsByStatus(
            UserAccount admin,
            String status) {

        if (!isAuthorized(admin)) {
            throw new SecurityException("Access denied.");
        }

        if (status == null
                || (!status.equalsIgnoreCase("ACTIVE")
                && !status.equalsIgnoreCase("INACTIVE"))) {

            throw new IllegalArgumentException(
                    "Status must be ACTIVE or INACTIVE."
            );
        }

        List<UserAccount> allAccounts =
                userAccountDAO.getAllAccounts();

        List<UserAccount> filteredAccounts =
                new ArrayList<>();

        for (UserAccount account : allAccounts) {

            if (status.equalsIgnoreCase(account.getAccountStatus())) {

                account.setPasswordHash(null);
                filteredAccounts.add(account);
            }
        }

        return filteredAccounts;
    }
    // =====================================================
// 9. FILTER ACCOUNTS BY ROLE AND STATUS
// =====================================================

public List<UserAccount> getAccountsByRoleAndStatus(
        UserAccount admin,
        String role,
        String status) {

    if (!isAuthorized(admin)) {
        throw new SecurityException("Access denied.");
    }

    if (role == null || role.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Role cannot be empty."
        );
    }

    if (status == null
            || (!status.equalsIgnoreCase("ACTIVE")
            && !status.equalsIgnoreCase("INACTIVE"))) {

        throw new IllegalArgumentException(
                "Status must be ACTIVE or INACTIVE."
        );
    }

    List<UserAccount> allAccounts =
            userAccountDAO.getAllAccounts();

    List<UserAccount> filteredAccounts = new ArrayList<>();

    for (UserAccount account : allAccounts) {

        if (role.trim().equalsIgnoreCase(account.getRole())
                && status.equalsIgnoreCase(
                        account.getAccountStatus())) {

            account.setPasswordHash(null);
            filteredAccounts.add(account);
        }
    }

    return filteredAccounts;
}
}
