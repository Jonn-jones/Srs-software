package com.spas.crss.security;

import com.spas.crss.model.UserAccount;

public class AccessControl {

    public static boolean hasPermission(
            UserAccount user,
            Permission permission) {

        if (user == null || permission == null) {
            return false;
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
            return false;
        }

        UserRole role;

        try {
            role = UserRole.valueOf(
                    user.getRole().toUpperCase()
            );
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }

        switch (role) {

            case STUDENT:
                return checkStudentPermissions(permission);

            case LECTURER:
                return checkLecturerPermissions(permission);

            case COD:
                return true;

            default:
                return false;
        }
    }

    private static boolean checkStudentPermissions(
            Permission permission) {

        return switch (permission) {

            case VIEW_AVAILABLE_COURSES,
                 REGISTER_COURSE,
                 WITHDRAW_COURSE,
                 VIEW_OWN_REGISTRATIONS,
                 VIEW_OWN_TIMETABLE,
                 VIEW_OWN_COURSE_HISTORY -> true;

            default -> false;
        };
    }

    private static boolean checkLecturerPermissions(
            Permission permission) {

        return switch (permission) {

            case VIEW_ASSIGNED_COURSES,
                 VIEW_ENROLLED_STUDENTS,
                 VIEW_TEACHING_TIMETABLE -> true;

            default -> false;
        };
    }
}