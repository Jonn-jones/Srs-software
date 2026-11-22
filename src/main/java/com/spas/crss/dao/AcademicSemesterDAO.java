package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.AcademicSemester;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AcademicSemesterDAO {

    // 1. ADD SEMESTER
    public int addSemester(AcademicSemester semester) {

        String sql = "INSERT INTO academic_semester "
                + "(academic_year, semester_name, start_date, "
                + "end_date, semester_status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, semester.getAcademicYear());
            pstmt.setString(2, semester.getSemesterName());
            pstmt.setDate(3, semester.getStartDate());
            pstmt.setDate(4, semester.getEndDate());
            pstmt.setString(5, semester.getSemesterStatus());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error adding semester: " + e.getMessage());
        }

        return -1;
    }

    // 2. VIEW ALL SEMESTERS
    public List<AcademicSemester> getAllSemester() {

        List<AcademicSemester> semesters = new ArrayList<>();

        String sql = "SELECT * FROM academic_semester";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                AcademicSemester semester = new AcademicSemester();

                semester.setSemesterId(rs.getInt("semester_id"));
                semester.setAcademicYear(rs.getString("academic_year"));
                semester.setSemesterName(rs.getString("semester_name"));
                semester.setStartDate(rs.getDate("start_date"));
                semester.setEndDate(rs.getDate("end_date"));
                semester.setSemesterStatus(rs.getString("semester_status"));

                semesters.add(semester);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving semesters: " + e.getMessage());
        }

        return semesters;
    }

    // 3. UPDATE SEMESTER
    public boolean updateSemester(AcademicSemester semester) {

        String sql = "UPDATE academic_semester SET "
                + "academic_year = ?, "
                + "semester_name = ?, "
                + "start_date = ?, "
                + "end_date = ?, "
                + "semester_status = ? "
                + "WHERE semester_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, semester.getAcademicYear());
            pstmt.setString(2, semester.getSemesterName());
            pstmt.setDate(3, semester.getStartDate());
            pstmt.setDate(4, semester.getEndDate());
            pstmt.setString(5, semester.getSemesterStatus());
            pstmt.setInt(6, semester.getSemesterId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating semester: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE SEMESTER
    public boolean deleteSemester(int semesterId) {

        String sql = "DELETE FROM academic_semester "
                + "WHERE semester_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, semesterId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting semester: " + e.getMessage());
            return false;
        }
    }

    // 5. ACTIVATE SEMESTER
    public boolean activateSemester(int semesterId) {

        String sql = "UPDATE academic_semester "
                + "SET semester_status = 'Active' "
                + "WHERE semester_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, semesterId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error activating semester: " + e.getMessage());
            return false;
        }
    }

    // 6. GET ACTIVE SEMESTER
    public AcademicSemester getActiveSemester() {

        String sql = "SELECT * FROM academic_semester "
                + "WHERE LOWER(semester_status) = 'active' "
                + "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {

                AcademicSemester semester = new AcademicSemester();

                semester.setSemesterId(rs.getInt("semester_id"));
                semester.setAcademicYear(rs.getString("academic_year"));
                semester.setSemesterName(rs.getString("semester_name"));
                semester.setStartDate(rs.getDate("start_date"));
                semester.setEndDate(rs.getDate("end_date"));
                semester.setSemesterStatus(rs.getString("semester_status"));

                return semester;
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving active semester: "
                    + e.getMessage());
        }

        return null;
    }
}