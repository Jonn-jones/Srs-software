package com.spas.crss.service;

import com.spas.crss.dao.CodDAO;
import com.spas.crss.model.Cod;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class CodManagementService {

    private final CodDAO codDAO;

    public CodManagementService() {
        this.codDAO = new CodDAO();
    }

    // CREATE
    public boolean addCod(UserAccount user, Cod cod) {

        checkPermission(user);
        validateCod(cod);

        if (codDAO.getCodById(cod.getCodId()) != null) {
            throw new IllegalArgumentException(
                    "A CoD with this ID already exists."
            );
        }

        checkEmailAvailability(cod.getEmail(), null);

        return codDAO.addCod(cod);
    }

    // READ ONE
    public Cod getCodById(UserAccount user, String codId) {

        checkPermission(user);
        validateCodId(codId);

        return codDAO.getCodById(codId);
    }

    // READ ALL
    public List<Cod> getAllCods(UserAccount user) {

        checkPermission(user);

        return codDAO.getAllCods();
    }

    // UPDATE
    public boolean updateCod(UserAccount user, Cod cod) {

        checkPermission(user);
        validateCod(cod);

        if (codDAO.getCodById(cod.getCodId()) == null) {
            throw new IllegalArgumentException(
                    "CoD record does not exist."
            );
        }

        checkEmailAvailability(cod.getEmail(), cod.getCodId());

        return codDAO.updateCod(cod);
    }

    // DELETE
    public boolean deleteCod(UserAccount user, String codId) {

        checkPermission(user);
        validateCodId(codId);

        if (codDAO.getCodById(codId) == null) {
            throw new IllegalArgumentException(
                    "CoD record does not exist."
            );
        }

        return codDAO.deleteCod(codId);
    }

    // VALIDATION
    private void validateCod(Cod cod) {

        if (cod == null) {
            throw new IllegalArgumentException("CoD details cannot be null.");
        }

        validateCodId(cod.getCodId());

        if (cod.getName() == null || cod.getName().isBlank()) {
            throw new IllegalArgumentException("CoD name is required.");
        }

        if (cod.getName().length() > 100) {
            throw new IllegalArgumentException(
                    "CoD name cannot exceed 100 characters."
            );
        }

        if (cod.getEmail() != null && !cod.getEmail().isBlank()) {
            String email = cod.getEmail().trim();

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException(
                        "Invalid email address."
                );
            }
        }
    }

    private void validateCodId(String codId) {

        if (codId == null || codId.isBlank()) {
            throw new IllegalArgumentException("CoD ID is required.");
        }

        if (codId.length() > 20) {
            throw new IllegalArgumentException(
                    "CoD ID cannot exceed 20 characters."
            );
        }
    }

    private void checkEmailAvailability(String email, String currentCodId) {

        if (email == null || email.isBlank()) {
            return;
        }

        boolean emailExists = codDAO.getAllCods().stream()
                .anyMatch(existing ->
                        email.trim().equalsIgnoreCase(existing.getEmail())
                        && !existing.getCodId().equals(currentCodId)
                );

        if (emailExists) {
            throw new IllegalArgumentException(
                    "This email address is already assigned to another CoD."
            );
        }
    }

    private void checkPermission(UserAccount user) {

        if (!AccessControl.hasPermission(
                user,
                Permission.MANAGE_COD_PROFILES)) {

            throw new SecurityException(
                    "You are not authorized to manage CoD profiles."
            );
        }
    }
}
