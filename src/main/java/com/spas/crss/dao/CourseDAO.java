package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // 1. Add a new course
    public boolean addCourse(Course course) {

        String sql = "INSERT INTO course "
                + "(course_title, course_code, credit_hours, description) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, course.getCourseTitle());
            statement.setString(2, course.getCourseCode());
            statement.setInt(3, course.getCreditHours());
            statement.setString(4, course.getDescription());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        course.setCourseId(generatedKeys.getInt(1));
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }

        return false;
    }

    // 2. Search for a course by ID
    public Course getCourseById(int courseId) {

        String sql = "SELECT * FROM course WHERE course_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return extractCourse(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching course: " + e.getMessage());
        }

        return null;
    }

    // 3. Retrieve all courses
    public List<Course> getAllCourses() {

        List<Course> courses = new ArrayList<>();

        String sql = "SELECT * FROM course ORDER BY course_code";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                courses.add(extractCourse(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving courses: " + e.getMessage());
        }

        return courses;
    }

    // 4. Update course details
    public boolean updateCourse(Course course) {

        String sql = "UPDATE course SET course_title = ?, "
                + "course_code = ?, credit_hours = ?, description = ? "
                + "WHERE course_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, course.getCourseTitle());
            statement.setString(2, course.getCourseCode());
            statement.setInt(3, course.getCreditHours());
            statement.setString(4, course.getDescription());
            statement.setInt(5, course.getCourseId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating course: " + e.getMessage());
        }

        return false;
    }

    // 5. Delete a course
    public boolean deleteCourse(int courseId) {

        String sql = "DELETE FROM course WHERE course_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }

        return false;
    }

    // Helper method to convert database records into Course objects
    private Course extractCourse(ResultSet result) throws SQLException {

        return new Course(
                result.getInt("course_id"),
                result.getString("course_title"),
                result.getString("course_code"),
                result.getInt("credit_hours"),
                result.getString("description")
        );
    }
}