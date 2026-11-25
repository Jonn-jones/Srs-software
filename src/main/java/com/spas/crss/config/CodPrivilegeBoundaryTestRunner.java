package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

public class CodPrivilegeBoundaryTestRunner {

    public static void main(String[] args) {

        UserAccountDAO accountDAO = new UserAccountDAO();

        UserAccount cod = accountDAO.getAccountByUsername("Dismus");

        System.out.println("======================================");
        System.out.println("CoD PRIVILEGE BOUNDARY TEST");
        System.out.println("======================================");

        if (cod == null) {
            System.out.println("CoD account not found.");
            return;
        }

        checkPermission(cod, Permission.MANAGE_COD_PROFILES);
        checkPermission(cod, Permission.MANAGE_USER_ACCOUNTS);
        checkPermission(cod, Permission.VIEW_SYSTEM_OVERVIEW);

        System.out.println("======================================");
    }

    private static void checkPermission(
            UserAccount user, Permission permission) {

        boolean allowed = AccessControl.hasPermission(user, permission);

        System.out.println(
                permission + ": " + (allowed ? "FAILED - ACCESS GRANTED"
                        : "PASSED - ACCESS DENIED")
        );
    }
}
