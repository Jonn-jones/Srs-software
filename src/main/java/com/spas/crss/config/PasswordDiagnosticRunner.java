package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.PasswordUtil;
import java.io.Console;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordDiagnosticRunner {

    public static void main(String[] args) {

        Console console = System.console();

        if (console == null) {
            System.out.println("Run this program directly in PowerShell.");
            return;
        }

        char[] passwordChars =
                console.readPassword("Enter the new Pixelpp password: ");

        String password = new String(passwordChars);

        try {
            UserAccountDAO dao = new UserAccountDAO();

            UserAccount account = dao.getAccountByUsername("Pixelpp");

            if (account == null) {
                System.out.println("RESULT: Account retrieval FAILED.");
                return;
            }

            System.out.println("Account retrieval: SUCCESS");
            System.out.println("Account status: " + account.getAccountStatus());
            System.out.println("Account role: " + account.getRole());

            String hash = account.getPasswordHash();

            System.out.println("Password hash retrieved: " +
                    (hash != null && !hash.isBlank()));

            boolean directCheck = hash != null &&
                    BCrypt.checkpw(password, hash);

            System.out.println("Direct BCrypt verification: " + directCheck);

            boolean utilityCheck =
                    PasswordUtil.verifyPassword(password, hash);

            System.out.println("PasswordUtil verification: " + utilityCheck);

        } catch (Exception e) {
            System.out.println("DIAGNOSTIC ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
