package com.spas.crss.config;

import com.spas.crss.dao.StudentDAO;
import com.spas.crss.model.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentTestRunner {

    public static void main(String[] args) {

        StudentDAO studentDAO = new StudentDAO();

        try {
            List<Student> students = studentDAO.getAllStudents();

            System.out.println("====================================");
            System.out.println("       SPAS CRSS - STUDENTS");
            System.out.println("====================================");

            for (Student student : students) {
                System.out.println("Student ID: " + student.getStudentId());
                System.out.println("Name: " + student.getName());
                System.out.println("Programme: " + student.getProgramme());
                System.out.println("Year: " + student.getYear());
                System.out.println("Email: " + student.getEmail());
                System.out.println("------------------------------------");
            }

            System.out.println("Total Students: " + students.size());

        } catch (SQLException e) {
            System.out.println("Error retrieving students: " + e.getMessage());
        }
    }
}