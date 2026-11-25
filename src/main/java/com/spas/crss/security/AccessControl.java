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
            role = UserRole.valueOf(user.getRole().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }

        return switch (role) {
            case STUDENT -> checkStudentPermissions(permission);
            case LECTURER -> checkLecturerPermissions(permission);
            case COD -> checkCodPermissions(permission);
            case SYSTEM_ADMIN -> checkSystemAdminPermissions(permission);
        };
    }

    // STUDENT PERMISSIONS
    private static boolean checkStudentPermissions(Permission permission) {
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

    // LECTURER PERMISSIONS
    private static boolean checkLecturerPermissions(Permission permission) {
        return switch (permission) {
            case VIEW_ASSIGNED_COURSES,
                 VIEW_ENROLLED_STUDENTS,
                 VIEW_TEACHING_TIMETABLE -> true;

            default -> false;
        };
    }

    // COORDINATOR OF DEPARTMENT PERMISSIONS
    private static boolean checkCodPermissions(Permission permission) {
        return switch (permission) {
            case MANAGE_STUDENTS,
                 MANAGE_LECTURERS,
                 MANAGE_COURSES,
                 MANAGE_SEMESTERS,
                 MANAGE_SPECIALIZATIONS,
                 MANAGE_PREREQUISITES,
                 MANAGE_COURSE_ASSIGNMENTS,
                 MANAGE_VENUES,
                 MANAGE_SCHEDULES,
                 MANAGE_STUDENT_COURSE_HISTORY,
                 VIEW_ALL_REGISTRATIONS -> true;

            default -> false;
        };
    }

    // SYSTEM ADMINISTRATOR PERMISSIONS
    private static boolean checkSystemAdminPermissions(Permission permission) {
        return switch (permission) {
            case MANAGE_COD_PROFILES,
                 MANAGE_USER_ACCOUNTS,
                 VIEW_SYSTEM_OVERVIEW -> true;

            default -> false;
        };
    }
}