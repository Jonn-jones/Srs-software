package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.Lecturer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    // 1. Add Lecturer
    public boolean addLecturer(Lecturer lecturer) {

        String sql = "INSERT INTO lecturer " +
                "(lecturer_id, name, qualification, email, department) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lecturer.getLecturerId());
            stmt.setString(2, lecturer.getName());
            stmt.setString(3, lecturer.getQualification());
            stmt.setString(4, lecturer.getEmail());
            stmt.setString(5, lecturer.getDepartment());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding lecturer: " + e.getMessage());
            return false;
        }
    }

    // 2. Get Lecturer By ID
    public Lecturer getLecturerById(String lecturerId) {

        String sql = "SELECT * FROM lecturer WHERE lecturer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lecturerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Lecturer(
                        rs.getString("lecturer_id"),
                        rs.getString("name"),
                        rs.getString("qualification"),
                        rs.getString("email"),
                        rs.getString("department")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving lecturer: " + e.getMessage());
        }

        return null;
    }

    // 3. Get All Lecturers
    public List<Lecturer> getAllLecturers() {

        List<Lecturer> lecturers = new ArrayList<>();

        String sql = "SELECT * FROM lecturer";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Lecturer lecturer = new Lecturer(
                        rs.getString("lecturer_id"),
                        rs.getString("name"),
                        rs.getString("qualification"),
                        rs.getString("email"),
                        rs.getString("department")
                );

                lecturers.add(lecturer);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving lecturers: " + e.getMessage());
        }

        return lecturers;
    }

    // 4. Update Lecturer
    public boolean updateLecturer(Lecturer lecturer) {

        String sql = "UPDATE lecturer SET name = ?, qualification = ?, " +
                "email = ?, department = ? WHERE lecturer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lecturer.getName());
            stmt.setString(2, lecturer.getQualification());
            stmt.setString(3, lecturer.getEmail());
            stmt.setString(4, lecturer.getDepartment());
            stmt.setString(5, lecturer.getLecturerId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating lecturer: " + e.getMessage());
            return false;
        }
    }

    // 5. Delete Lecturer
    public boolean deleteLecturer(String lecturerId) {

        String sql = "DELETE FROM lecturer WHERE lecturer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lecturerId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting lecturer: " + e.getMessage());
            return false;
        }
    }
}