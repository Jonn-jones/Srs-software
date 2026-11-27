package com.spas.crss.service;

import com.spas.crss.dao.LecturerSpecializationDAO;
import com.spas.crss.model.TeachingSpecialization;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class LecturerSpecializationService {

    private final LecturerSpecializationDAO specializationDAO;

    public LecturerSpecializationService() {
        this.specializationDAO = new LecturerSpecializationDAO();
    }

    // CREATE SPECIALIZATION
    public int addSpecialization(
            UserAccount user,
            TeachingSpecialization specialization) {

        checkPermission(user);
        validateSpecialization(specialization);

        specialization.setSpecializationName(
                specialization.getSpecializationName().trim()
        );

        if (specialization.getDescription() != null) {
            specialization.setDescription(
                    specialization.getDescription().trim()
            );
        }

        return specializationDAO.addSpecialization(specialization);
    }

    // RETRIEVE ALL SPECIALIZATIONS
    public List<TeachingSpecialization> getAllSpecializations(
            UserAccount user) {

        checkPermission(user);

        return specializationDAO.getAllSpecializations();
    }

    // ASSIGN SPECIALIZATION TO LECTURER
    public String assignSpecialization(
            UserAccount user,
            String lecturerId,
            int specializationId) {

        checkPermission(user);
        validateLecturerId(lecturerId);
        validateSpecializationId(specializationId);

        return specializationDAO.assignSpecialization(
                lecturerId.trim(),
                specializationId
        );
    }

    // VIEW LECTURER SPECIALIZATIONS
    public List<TeachingSpecialization> getLecturerSpecializations(
            UserAccount user,
            String lecturerId) {

        checkPermission(user);
        validateLecturerId(lecturerId);

        return specializationDAO.getLecturerSpecializations(
                lecturerId.trim()
        );
    }

    // REMOVE SPECIALIZATION FROM LECTURER
    public boolean removeSpecialization(
            UserAccount user,
            String lecturerId,
            int specializationId) {

        checkPermission(user);
        validateLecturerId(lecturerId);
        validateSpecializationId(specializationId);

        return specializationDAO.removeSpecialization(
                lecturerId.trim(),
                specializationId
        );
    }

    // VALIDATE SPECIALIZATION
    private void validateSpecialization(
            TeachingSpecialization specialization) {

        if (specialization == null) {
            throw new IllegalArgumentException(
                    "Specialization details cannot be null."
            );
        }

        if (specialization.getSpecializationName() == null
                || specialization.getSpecializationName().isBlank()) {

            throw new IllegalArgumentException(
                    "Specialization name is required."
            );
        }

        if (specialization.getDescription() != null
                && specialization.getDescription().length() > 1000) {

            throw new IllegalArgumentException(
                    "Description cannot exceed 1000 characters."
            );
        }
    }

    // VALIDATE LECTURER ID
    private void validateLecturerId(String lecturerId) {

        if (lecturerId == null || lecturerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Lecturer ID is required."
            );
        }
    }

    // VALIDATE SPECIALIZATION ID
    private void validateSpecializationId(int specializationId) {

        if (specializationId <= 0) {
            throw new IllegalArgumentException(
                    "Specialization ID must be a positive number."
            );
        }
    }

    // CHECK PERMISSIONS
    private void checkPermission(UserAccount user) {

        if (!AccessControl.hasPermission(
                user,
                Permission.MANAGE_SPECIALIZATIONS)) {

            throw new SecurityException(
                    "You are not authorized to manage specializations."
            );
        }
    }
}
