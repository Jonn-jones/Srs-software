package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // 1. ADD STUDENT
    public boolean addStudent(Student student) throws SQLException {

        String sql = "INSERT INTO student " +
                "(student_id, name, programme, `year`, email) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getStudentId());
            statement.setString(2, student.getName());
            statement.setString(3, student.getProgramme());
            statement.setInt(4, student.getYear());
            statement.setString(5, student.getEmail());

            return statement.executeUpdate() > 0;
        }
    }


    // 2. GET STUDENT BY ID
    public Student getStudentById(String studentId) throws SQLException {

        String sql = "SELECT * FROM student WHERE student_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new Student(
                            result.getString("student_id"),
                            result.getString("name"),
                            result.getString("programme"),
                            result.getInt("year"),
                            result.getString("email")
                    );
                }
            }
        }

        return null;
    }


    // 3. GET ALL STUDENTS
    public List<Student> getAllStudents() throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM student";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Student student = new Student(
                        result.getString("student_id"),
                        result.getString("name"),
                        result.getString("programme"),
                        result.getInt("year"),
                        result.getString("email")
                );

                students.add(student);
            }
        }

        return students;
    }


    // 4. UPDATE STUDENT
    public boolean updateStudent(Student student) throws SQLException {

        String sql = "UPDATE student SET name = ?, programme = ?, " +
                "`year` = ?, email = ? WHERE student_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getName());
            statement.setString(2, student.getProgramme());
            statement.setInt(3, student.getYear());
            statement.setString(4, student.getEmail());
            statement.setString(5, student.getStudentId());

            return statement.executeUpdate() > 0;
        }
    }


    // 5. DELETE STUDENT
    public boolean deleteStudent(String studentId) throws SQLException {

        String sql = "DELETE FROM student WHERE student_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);

            return statement.executeUpdate() > 0;
        }
    }
}