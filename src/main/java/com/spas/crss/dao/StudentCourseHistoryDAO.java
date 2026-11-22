
package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.StudentCourseHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentCourseHistoryDAO {

    // 1. Record a student's course attempt
    public boolean addHistory(StudentCourseHistory history) {

        String sql = "INSERT INTO student_course_history "
                + "(student_id, course_id, semester_id, passed) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, history.getStudentId());
            statement.setInt(2, history.getCourseId());
            statement.setInt(3, history.getSemesterId());
            statement.setString(4, history.isPassed() ? "Yes" : "No");

            int rows = statement.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        history.setHistoryId(keys.getInt(1));
                    }
                }
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error recording course history: " + e.getMessage(), e);
        }
    }

    // 2. Retrieve all attempts made by a student
    public List<StudentCourseHistory> getHistoryByStudent(String studentId) {

        List<StudentCourseHistory> historyList = new ArrayList<>();

        String sql = "SELECT * FROM student_course_history "
                + "WHERE student_id = ? "
                + "ORDER BY semester_id, course_id, history_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    historyList.add(extractHistory(result));
                }
            }

            return historyList;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error retrieving student history: " + e.getMessage(), e);
        }
    }

    // 3. Retrieve all attempts for a particular course
    public List<StudentCourseHistory> getHistoryByCourse(int courseId) {

        List<StudentCourseHistory> historyList = new ArrayList<>();

        String sql = "SELECT * FROM student_course_history "
                + "WHERE course_id = ? "
                + "ORDER BY semester_id, student_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    historyList.add(extractHistory(result));
                }
            }

            return historyList;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error retrieving course history: " + e.getMessage(), e);
        }
    }

    // 4. Check whether a student has ever passed a course
    public boolean hasPassedCourse(String studentId, int courseId) {

        String sql = "SELECT COUNT(*) FROM student_course_history "
                + "WHERE student_id = ? "
                + "AND course_id = ? "
                + "AND passed = 'Yes'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            statement.setInt(2, courseId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }

            throw new SQLException("Could not retrieve course history result.");

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error checking course history: " + e.getMessage(), e);
        }
    }

    // 5. Update an existing academic result
    public boolean updateResult(int historyId, boolean passed) {

        String sql = "UPDATE student_course_history "
                + "SET passed = ? "
                + "WHERE history_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, passed ? "Yes" : "No");
            statement.setInt(2, historyId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error updating course result: " + e.getMessage(), e);
        }
    }

    // Extract database records into Java objects
    private StudentCourseHistory extractHistory(ResultSet result)
            throws SQLException {

        return new StudentCourseHistory(
                result.getInt("history_id"),
                result.getString("student_id"),
                result.getInt("course_id"),
                result.getInt("semester_id"),
                "Yes".equalsIgnoreCase(result.getString("passed"))
        );
    }
}