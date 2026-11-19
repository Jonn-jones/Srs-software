package com.spas.crss.config;

import com.spas.crss.dao.StudentDAO;
import com.spas.crss.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class StudentManagementMenu {

    private static final Scanner input = new Scanner(System.in);
    private static final StudentDAO studentDAO = new StudentDAO();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n====================================");
            System.out.println("       SPAS CRSS");
            System.out.println("       STUDENT MANAGEMENT");
            System.out.println("====================================");
            System.out.println("1. Register Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("0. Exit");
            System.out.println("====================================");

            System.out.print("Enter your choice: ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {

                case 1:
                    registerStudent();
                    break;

                case 2:
                    viewAllStudents();
                    break;

                case 3:
                    searchStudent();
                    break;

                case 4:
                    updateStudent();
                    break;

                case 5:
                    deleteStudent();
                    break;

                case 0:
                    System.out.println("Exiting Student Management...");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 0);
    }

    // REGISTER STUDENT
    private static void registerStudent() {

        try {
            System.out.println("\n--- REGISTER STUDENT ---");

            System.out.print("Student ID: ");
            String id = input.nextLine();

            System.out.print("Full Name: ");
            String name = input.nextLine();

            System.out.print("Programme: ");
            String programme = input.nextLine();

            System.out.print("Year of Study: ");
            int year = Integer.parseInt(input.nextLine());

            System.out.print("Email: ");
            String email = input.nextLine();

            Student student = new Student(
                    id, name, programme, year, email
            );

            if (studentDAO.addStudent(student)) {
                System.out.println("Student registered successfully!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Year must be a valid number.");

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // VIEW ALL STUDENTS
    private static void viewAllStudents() {

        try {
            System.out.println("\n--- REGISTERED STUDENTS ---");

            List<Student> students = studentDAO.getAllStudents();

            if (students.isEmpty()) {
                System.out.println("No students found.");
                return;
            }

            for (Student student : students) {

                System.out.println("------------------------------------");
                System.out.println("ID: " + student.getStudentId());
                System.out.println("Name: " + student.getName());
                System.out.println("Programme: " + student.getProgramme());
                System.out.println("Year: " + student.getYear());
                System.out.println("Email: " + student.getEmail());
            }

            System.out.println("------------------------------------");
            System.out.println("Total Students: " + students.size());

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // SEARCH STUDENT
    private static void searchStudent() {

        try {
            System.out.println("\n--- SEARCH STUDENT ---");

            System.out.print("Enter Student ID: ");
            String id = input.nextLine();

            Student student = studentDAO.getStudentById(id);

            if (student == null) {
                System.out.println("Student not found.");
            } else {
                System.out.println("ID: " + student.getStudentId());
                System.out.println("Name: " + student.getName());
                System.out.println("Programme: " + student.getProgramme());
                System.out.println("Year: " + student.getYear());
                System.out.println("Email: " + student.getEmail());
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    // UPDATE STUDENT
    private static void updateStudent() {

        try {
            System.out.println("\n--- UPDATE STUDENT ---");

            System.out.print("Enter Student ID: ");
            String id = input.nextLine();

            Student student = studentDAO.getStudentById(id);

            if (student == null) {
                System.out.println("Student not found.");
                return;
            }

            System.out.println("Leave a field empty to keep its current value.");

            System.out.print("New Name (" + student.getName() + "): ");
            String name = input.nextLine();

            System.out.print("New Programme (" + student.getProgramme() + "): ");
            String programme = input.nextLine();

            System.out.print("New Year (" + student.getYear() + "): ");
            String yearInput = input.nextLine();

            System.out.print("New Email (" + student.getEmail() + "): ");
            String email = input.nextLine();

            if (!name.isBlank()) {
                student.setName(name);
            }

            if (!programme.isBlank()) {
                student.setProgramme(programme);
            }

            if (!yearInput.isBlank()) {
                student.setYear(Integer.parseInt(yearInput));
            }

            if (!email.isBlank()) {
                student.setEmail(email);
            }

            if (studentDAO.updateStudent(student)) {
                System.out.println("Student updated successfully!");
            } else {
                System.out.println("No changes were made.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid year entered.");

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // DELETE STUDENT
    private static void deleteStudent() {

        try {
            System.out.println("\n--- DELETE STUDENT ---");

            System.out.print("Enter Student ID: ");
            String id = input.nextLine();

            Student student = studentDAO.getStudentById(id);

            if (student == null) {
                System.out.println("Student not found.");
                return;
            }

            System.out.println("Student: " + student.getName());

            System.out.print("Are you sure you want to delete this student? (Y/N): ");
            String confirmation = input.nextLine();

            if (confirmation.equalsIgnoreCase("Y")) {

                if (studentDAO.deleteStudent(id)) {
                    System.out.println("Student deleted successfully!");
                }

            } else {
                System.out.println("Deletion cancelled.");
            }

        } catch (SQLException e) {
            System.out.println("Deletion failed: " + e.getMessage());
        }
    }
}