package com.spas.crss.config;

import com.spas.crss.dao.StudentDAO;
import com.spas.crss.model.Student;

import java.sql.SQLException;
import java.util.Scanner;

public class StudentRegistrationTest {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        StudentDAO studentDAO = new StudentDAO();

        System.out.println("====================================");
        System.out.println("       SPAS - STUDENT REGISTRATION");
        System.out.println("====================================");

        System.out.print("Enter Student ID: ");
        String studentId = input.nextLine();

        System.out.print("Enter Full Name: ");
        String name = input.nextLine();

        System.out.print("Enter Programme: ");
        String programme = input.nextLine();

        System.out.print("Enter Year of Study: ");
        int year = input.nextInt();
        input.nextLine();

        System.out.print("Enter Email: ");
        String email = input.nextLine();

        Student student = new Student(
                studentId,
                name,
                programme,
                year,
                email
        );

        try {

            boolean registered = studentDAO.addStudent(student);

            if (registered) {
                System.out.println("\nStudent registered successfully!");
            } else {
                System.out.println("\nStudent registration failed.");
            }

        } catch (SQLException e) {

            System.out.println("\nRegistration error: " + e.getMessage());
        }

        input.close();
    }
}