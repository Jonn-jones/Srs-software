package com.spas.crss.config;

import com.spas.crss.dao.LecturerDAO;
import com.spas.crss.model.Lecturer;

import java.util.Scanner;

public class LecturerRegistrationTest {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        LecturerDAO lecturerDAO = new LecturerDAO();

        System.out.println("=================================");
        System.out.println("     LECTURER REGISTRATION");
        System.out.println("=================================");

        System.out.print("Enter Lecturer ID: ");
        String id = input.nextLine();

        System.out.print("Enter Full Name: ");
        String name = input.nextLine();

        System.out.print("Enter Qualification: ");
        String qualification = input.nextLine();

        System.out.print("Enter Email: ");
        String email = input.nextLine();

        System.out.print("Enter Department: ");
        String department = input.nextLine();

        Lecturer lecturer = new Lecturer(
                id, name, qualification, email, department
        );

        boolean success = lecturerDAO.addLecturer(lecturer);

        if (success) {
            System.out.println("\nLecturer registered successfully!");
        } else {
            System.out.println("\nLecturer registration failed!");
        }

        input.close();
    }
}