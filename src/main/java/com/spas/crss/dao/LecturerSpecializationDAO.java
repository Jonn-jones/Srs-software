package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.TeachingSpecialization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LecturerSpecializationDAO {

    private static final int MAX_SPECIALIZATIONS = 2;

    // 1. Add a new teaching specialization
    public int addSpecialization(TeachingSpecialization specialization) {

        String sql = "INSERT INTO teaching_specialization "
                + "(specialization_name, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, specialization.getSpecializationName());
            pstmt.setString(2, specialization.getDescription());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error adding specialization: "
                    + e.getMessage());
        }

        return -1;
    }

    // 2. Retrieve all teaching specializations
    public List<TeachingSpecialization> getAllSpecializations() {

        List<TeachingSpecialization> specializations = new ArrayList<>();

        String sql = "SELECT * FROM teaching_specialization";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                TeachingSpecialization specialization =
                        new TeachingSpecialization();

                specialization.setSpecializationId(
                        rs.getInt("specialization_id"));

                specialization.setSpecializationName(
                        rs.getString("specialization_name"));

                specialization.setDescription(
                        rs.getString("description"));

                specializations.add(specialization);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving specializations: "
                    + e.getMessage());
        }

        return specializations;
    }

    // 3. Assign specialization to a lecturer
    public String assignSpecialization(
            String lecturerId, int specializationId) {

        String lockSql =
                "SELECT lecturer_id FROM lecturer "
                + "WHERE lecturer_id = ? FOR UPDATE";

        String duplicateSql =
                "SELECT COUNT(*) FROM lecturer_specialization "
                + "WHERE lecturer_id = ? AND specialization_id = ?";

        String countSql =
                "SELECT COUNT(*) FROM lecturer_specialization "
                + "WHERE lecturer_id = ?";

        String insertSql =
                "INSERT INTO lecturer_specialization "
                + "(lecturer_id, specialization_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                // Check and lock lecturer record
                try (PreparedStatement pstmt =
                             conn.prepareStatement(lockSql)) {

                    pstmt.setString(1, lecturerId);

                    try (ResultSet rs = pstmt.executeQuery()) {

                        if (!rs.next()) {
                            conn.rollback();
                            return "Lecturer not found.";
                        }
                    }
                }

                // Check whether specialization exists
                String specializationSql =
                        "SELECT specialization_id "
                        + "FROM teaching_specialization "
                        + "WHERE specialization_id = ?";

                try (PreparedStatement pstmt =
                             conn.prepareStatement(specializationSql)) {

                    pstmt.setInt(1, specializationId);

                    try (ResultSet rs = pstmt.executeQuery()) {

                        if (!rs.next()) {
                            conn.rollback();
                            return "Specialization not found.";
                        }
                    }
                }

                // Prevent duplicate assignment
                try (PreparedStatement pstmt =
                             conn.prepareStatement(duplicateSql)) {

                    pstmt.setString(1, lecturerId);
                    pstmt.setInt(2, specializationId);

                    try (ResultSet rs = pstmt.executeQuery()) {

                        if (rs.next() && rs.getInt(1) > 0) {
                            conn.rollback();
                            return "This specialization is already assigned.";
                        }
                    }
                }

                // Enforce maximum of two specializations
                try (PreparedStatement pstmt =
                             conn.prepareStatement(countSql)) {

                    pstmt.setString(1, lecturerId);

                    try (ResultSet rs = pstmt.executeQuery()) {

                        if (rs.next()
                                && rs.getInt(1) >= MAX_SPECIALIZATIONS) {

                            conn.rollback();

                            return "Assignment failed! A lecturer can "
                                    + "have a maximum of two specializations.";
                        }
                    }
                }

                // Insert assignment
                try (PreparedStatement pstmt =
                             conn.prepareStatement(insertSql)) {

                    pstmt.setString(1, lecturerId);
                    pstmt.setInt(2, specializationId);

                    pstmt.executeUpdate();
                }

                conn.commit();

                return "Specialization assigned successfully.";

            } catch (SQLException e) {

                conn.rollback();

                return "Assignment failed: " + e.getMessage();

            } finally {

                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {

            return "Database error: " + e.getMessage();
        }
    }

    // 4. View specializations assigned to a lecturer
    public List<TeachingSpecialization> getLecturerSpecializations(
            String lecturerId) {

        List<TeachingSpecialization> specializations = new ArrayList<>();

        String sql =
                "SELECT ts.specialization_id, "
                + "ts.specialization_name, ts.description "
                + "FROM teaching_specialization ts "
                + "INNER JOIN lecturer_specialization ls "
                + "ON ts.specialization_id = ls.specialization_id "
                + "WHERE ls.lecturer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lecturerId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    TeachingSpecialization specialization =
                            new TeachingSpecialization();

                    specialization.setSpecializationId(
                            rs.getInt("specialization_id"));

                    specialization.setSpecializationName(
                            rs.getString("specialization_name"));

                    specialization.setDescription(
                            rs.getString("description"));

                    specializations.add(specialization);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving lecturer specializations: "
                    + e.getMessage());
        }

        return specializations;
    }

    // 5. Remove a specialization from a lecturer
    public boolean removeSpecialization(
            String lecturerId, int specializationId) {

        String sql =
                "DELETE FROM lecturer_specialization "
                + "WHERE lecturer_id = ? AND specialization_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lecturerId);
            pstmt.setInt(2, specializationId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error removing specialization: "
                    + e.getMessage());

            return false;
        }
    }
}