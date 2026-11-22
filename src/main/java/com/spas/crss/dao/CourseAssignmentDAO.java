
package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.CourseAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseAssignmentDAO {

    // 1. Assign a course to a lecturer
    public boolean assignCourse(CourseAssignment assignment) {

        String sql = "INSERT INTO course_assignment "
                + "(course_id, lecturer_id, semester_id) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, assignment.getCourseId());
            pstmt.setString(2, assignment.getLecturerId());
            pstmt.setInt(3, assignment.getSemesterId());

            int rowsInserted = pstmt.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                System.out.println(
                        "This course already has a lecturer assigned in this semester."
                );
            } else {
                System.out.println(
                        "Error assigning course: " + e.getMessage()
                );
            }

            return false;
        }
    }

    // 2. Retrieve all course assignments
    public List<CourseAssignment> getAllAssignments() {

        List<CourseAssignment> assignments = new ArrayList<>();

        String sql = "SELECT * FROM course_assignment";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                CourseAssignment assignment = new CourseAssignment();

                assignment.setAssignmentId(rs.getInt("assignment_id"));
                assignment.setCourseId(rs.getInt("course_id"));
                assignment.setLecturerId(rs.getString("lecturer_id"));
                assignment.setSemesterId(rs.getInt("semester_id"));

                assignments.add(assignment);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving assignments: " + e.getMessage()
            );
        }

        return assignments;
    }

    // 3. Retrieve assignments by lecturer
    public List<CourseAssignment> getAssignmentsByLecturer(String lecturerId) {

        List<CourseAssignment> assignments = new ArrayList<>();

        String sql = "SELECT * FROM course_assignment WHERE lecturer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lecturerId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    CourseAssignment assignment = new CourseAssignment();

                    assignment.setAssignmentId(rs.getInt("assignment_id"));
                    assignment.setCourseId(rs.getInt("course_id"));
                    assignment.setLecturerId(rs.getString("lecturer_id"));
                    assignment.setSemesterId(rs.getInt("semester_id"));

                    assignments.add(assignment);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving lecturer assignments: " + e.getMessage()
            );
        }

        return assignments;
    }

    // 4. Remove a course assignment
    public boolean removeAssignment(int assignmentId) {

        String sql = "DELETE FROM course_assignment WHERE assignment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, assignmentId);

            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error removing assignment: " + e.getMessage()
            );

            return false;
        }
    }
}