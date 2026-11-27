package com.spas.crss.service;

import com.spas.crss.dao.LecturerDAO;
import com.spas.crss.model.Lecturer;
import com.spas.crss.model.UserAccount;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;
import java.util.regex.Pattern;

public class LecturerManagementService {

    private final LecturerDAO lecturerDAO;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public LecturerManagementService() {
        this.lecturerDAO = new LecturerDAO();
    }

    // 1. Add lecturer
    public boolean addLecturer(UserAccount user, Lecturer lecturer) {

        checkPermission(user);
        validateLecturer(lecturer);

        if (lecturerDAO.getLecturerById(lecturer.getLecturerId()) != null) {
            throw new IllegalArgumentException(
                    "A lecturer with this ID already exists."
            );
        }

        checkDuplicateEmail(lecturer.getEmail(), null);

        return lecturerDAO.addLecturer(lecturer);
    }

    // 2. Retrieve lecturer by ID
    public Lecturer getLecturerById(UserAccount user, String lecturerId) {

        checkPermission(user);
        validateLecturerId(lecturerId);

        return lecturerDAO.getLecturerById(lecturerId.trim());
    }

    // 3. Retrieve all lecturers
    public List<Lecturer> getAllLecturers(UserAccount user) {

        checkPermission(user);

        return lecturerDAO.getAllLecturers();
    }

    // 4. Update lecturer
    public boolean updateLecturer(UserAccount user, Lecturer lecturer) {

        checkPermission(user);
        validateLecturer(lecturer);

        Lecturer existing =
                lecturerDAO.getLecturerById(lecturer.getLecturerId().trim());

        if (existing == null) {
            return false;
        }

        checkDuplicateEmail(
                lecturer.getEmail(),
                lecturer.getLecturerId().trim()
        );

        return lecturerDAO.updateLecturer(lecturer);
    }

    // 5. Delete lecturer
    public boolean deleteLecturer(UserAccount user, String lecturerId) {

        checkPermission(user);
        validateLecturerId(lecturerId);

        return lecturerDAO.deleteLecturer(lecturerId.trim());
    }

    // 6. Permission check
    private void checkPermission(UserAccount user) {

        if (!AccessControl.hasPermission(
                user,
                Permission.MANAGE_LECTURERS
        )) {
            throw new SecurityException(
                    "You do not have permission to manage lecturers."
            );
        }
    }

    // 7. Validate lecturer data
    private void validateLecturer(Lecturer lecturer) {

        if (lecturer == null) {
            throw new IllegalArgumentException(
                    "Lecturer details are required."
            );
        }

        validateLecturerId(lecturer.getLecturerId());

        if (lecturer.getName() == null ||
                lecturer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Lecturer name is required."
            );
        }

        if (lecturer.getName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Lecturer name cannot exceed 100 characters."
            );
        }

        if (lecturer.getQualification() != null &&
                lecturer.getQualification().length() > 100) {
            throw new IllegalArgumentException(
                    "Qualification cannot exceed 100 characters."
            );
        }

        if (lecturer.getDepartment() != null &&
                lecturer.getDepartment().length() > 100) {
            throw new IllegalArgumentException(
                    "Department cannot exceed 100 characters."
            );
        }

        validateEmail(lecturer.getEmail());
    }

    // 8. Validate lecturer ID
    private void validateLecturerId(String lecturerId) {

        if (lecturerId == null || lecturerId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Lecturer ID is required."
            );
        }

        if (lecturerId.trim().length() > 20) {
            throw new IllegalArgumentException(
                    "Lecturer ID cannot exceed 20 characters."
            );
        }
    }

    // 9. Validate optional email
    private void validateEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return;
        }

        if (email.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Email cannot exceed 100 characters."
            );
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException(
                    "Please provide a valid email address."
            );
        }
    }

    // 10. Check for duplicate email
    private void checkDuplicateEmail(
            String email,
            String currentLecturerId
    ) {

        if (email == null || email.trim().isEmpty()) {
            return;
        }

        List<Lecturer> lecturers = lecturerDAO.getAllLecturers();

        for (Lecturer existing : lecturers) {

            if (existing.getEmail() != null &&
                    existing.getEmail().equalsIgnoreCase(email.trim()) &&
                    !existing.getLecturerId().equals(currentLecturerId)) {

                throw new IllegalArgumentException(
                        "A lecturer with this email already exists."
                );
            }
        }
    }
}
