package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.Registration;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    // Register a student for a course
    public boolean createRegistration(Registration registration) {

        String sql = """
                INSERT INTO registration
                (student_id, course_id, semester_id,
                 registration_date, registration_status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, registration.getStudentId());
            statement.setInt(2, registration.getCourseId());
            statement.setInt(3, registration.getSemesterId());
            statement.setDate(4,
                    Date.valueOf(registration.getRegistrationDate()));
            statement.setString(5, registration.getRegistrationStatus());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {

                try (ResultSet generatedKeys =
                             statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        registration.setRegistrationId(
                                generatedKeys.getInt(1));
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }

        return false;
    }

    // Retrieve a registration by ID
    public Registration getRegistrationById(int registrationId) {

        String sql = """
                SELECT * FROM registration
                WHERE registration_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, registrationId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return extractRegistration(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving registration: "
                    + e.getMessage());
        }

        return null;
    }

    // Retrieve all registrations belonging to a student
    public List<Registration> getRegistrationsByStudent(String studentId) {

        List<Registration> registrations = new ArrayList<>();

        String sql = """
                SELECT * FROM registration
                WHERE student_id = ?
                ORDER BY registration_date DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, studentId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    registrations.add(extractRegistration(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving student registrations: "
                    + e.getMessage());
        }

        return registrations;
    }

    // Find an existing registration for a student, course and semester
    public Registration getRegistrationByStudentCourseSemester(
            String studentId,
            int courseId,
            int semesterId) {

        String sql = """
                SELECT * FROM registration
                WHERE student_id = ?
                AND course_id = ?
                AND semester_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            statement.setInt(2, courseId);
            statement.setInt(3, semesterId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return extractRegistration(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding existing registration: "
                    + e.getMessage());
        }

        return null;
    }

    // Check whether a student has already registered for a course
    public boolean registrationExists(
            String studentId,
            int courseId,
            int semesterId) {

        String sql = """
                SELECT COUNT(*) FROM registration
                WHERE student_id = ?
                AND course_id = ?
                AND semester_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            statement.setInt(2, courseId);
            statement.setInt(3, semesterId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking registration: "
                    + e.getMessage());
        }

        return false;
    }

    // Reactivate a previously withdrawn registration
    public boolean reactivateRegistration(
            String studentId,
            int courseId,
            int semesterId,
            LocalDate registrationDate) {

        String sql = """
                UPDATE registration
                SET registration_status = 'ACTIVE',
                    registration_date = ?
                WHERE student_id = ?
                AND course_id = ?
                AND semester_id = ?
                AND registration_status = 'WITHDRAWN'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(registrationDate));
            statement.setString(2, studentId);
            statement.setInt(3, courseId);
            statement.setInt(4, semesterId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Re-registration failed: " + e.getMessage());
        }

        return false;
    }

    // Withdraw a student from a course
    public boolean withdrawRegistration(int registrationId) {

        String sql = """
                UPDATE registration
                SET registration_status = 'WITHDRAWN'
                WHERE registration_id = ?
                AND registration_status = 'ACTIVE'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, registrationId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }

        return false;
    }

    // Convert database result into Registration object
    private Registration extractRegistration(ResultSet result)
            throws SQLException {

        Registration registration = new Registration();

        registration.setRegistrationId(
                result.getInt("registration_id"));

        registration.setStudentId(
                result.getString("student_id"));

        registration.setCourseId(
                result.getInt("course_id"));

        registration.setSemesterId(
                result.getInt("semester_id"));

        registration.setRegistrationDate(
                result.getDate("registration_date").toLocalDate());

        registration.setRegistrationStatus(
                result.getString("registration_status"));

        return registration;
    }
}