package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.CoursePrerequisite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoursePrerequisiteDAO {

    // 1. Add a course prerequisite
    public boolean addPrerequisite(CoursePrerequisite prerequisite) {

        if (prerequisite.getCourseId()
                == prerequisite.getPrerequisiteCourseId()) {

            System.out.println(
                    "A course cannot be its own prerequisite.");

            return false;
        }

        String sql =
                "INSERT INTO course_prerequisite "
                + "(course_id, prerequisite_course_id) "
                + "VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prerequisite.getCourseId());
            pstmt.setInt(2, prerequisite.getPrerequisiteCourseId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error adding prerequisite: " + e.getMessage());

            return false;
        }
    }

    // 2. Retrieve prerequisites for a course
    public List<CoursePrerequisite> getPrerequisites(int courseId) {

        List<CoursePrerequisite> prerequisites = new ArrayList<>();

        String sql =
                "SELECT course_id, prerequisite_course_id "
                + "FROM course_prerequisite "
                + "WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    CoursePrerequisite prerequisite =
                            new CoursePrerequisite();

                    prerequisite.setCourseId(
                            rs.getInt("course_id"));

                    prerequisite.setPrerequisiteCourseId(
                            rs.getInt("prerequisite_course_id"));

                    prerequisites.add(prerequisite);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error retrieving prerequisites: "
                    + e.getMessage());
        }

        return prerequisites;
    }

    // 3. Remove a prerequisite
    public boolean removePrerequisite(
            int courseId, int prerequisiteCourseId) {

        String sql =
                "DELETE FROM course_prerequisite "
                + "WHERE course_id = ? "
                + "AND prerequisite_course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            pstmt.setInt(2, prerequisiteCourseId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error removing prerequisite: "
                    + e.getMessage());

            return false;
        }
    }

    // 4. Check whether a student passed a prerequisite
    public boolean hasPassedPrerequisite(
            String studentId, int prerequisiteCourseId) {

        String sql =
                "SELECT COUNT(*) "
                + "FROM student_course_history "
                + "WHERE student_id = ? "
                + "AND course_id = ? "
                + "AND passed = 'Yes'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setInt(2, prerequisiteCourseId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error checking prerequisite completion: "
                    + e.getMessage());
        }

        return false;
    }

    // 5. Check whether a student can take a course
    public boolean canTakeCourse(String studentId, int courseId) {

        List<CoursePrerequisite> prerequisites =
                getPrerequisites(courseId);

        for (CoursePrerequisite prerequisite : prerequisites) {

            boolean passed = hasPassedPrerequisite(
                    studentId,
                    prerequisite.getPrerequisiteCourseId()
            );

            if (!passed) {
                return false;
            }
        }

        return true;
    }
}