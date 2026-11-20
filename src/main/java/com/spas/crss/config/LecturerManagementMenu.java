package com.spas.crss.config;

import com.spas.crss.dao.LecturerDAO;
import com.spas.crss.model.Lecturer;

import java.util.List;
import java.util.Scanner;

public class LecturerManagementMenu {

    private static final Scanner input = new Scanner(System.in);
    private static final LecturerDAO lecturerDAO = new LecturerDAO();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n========================================");
            System.out.println("       SPAS CRSS - LECTURER MANAGEMENT");
            System.out.println("========================================");
            System.out.println("1. Register New Lecturer");
            System.out.println("2. View All Lecturers");
            System.out.println("3. Search Lecturer");
            System.out.println("4. Update Lecturer Details");
            System.out.println("5. Delete Lecturer");
            System.out.println("0. Exit");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(input.nextLine());

                switch (choice) {

                    case 1:
                        registerLecturer();
                        break;

                    case 2:
                        viewAllLecturers();
                        break;

                    case 3:
                        searchLecturer();
                        break;

                    case 4:
                        updateLecturer();
                        break;

                    case 5:
                        deleteLecturer();
                        break;

                    case 0:
                        System.out.println("Returning from Lecturer Management...");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            } catch (NumberFormatException e) {
                choice = -1;
                System.out.println("Please enter a valid number.");
            }

        } while (choice != 0);

        input.close();
    }

    // 1. Register Lecturer
    private static void registerLecturer() {

        System.out.println("\n--- REGISTER NEW LECTURER ---");

        System.out.print("Lecturer ID: ");
        String id = input.nextLine().trim();

        System.out.print("Full Name: ");
        String name = input.nextLine().trim();

        System.out.print("Qualification: ");
        String qualification = input.nextLine().trim();

        System.out.print("Email: ");
        String email = input.nextLine().trim();

        System.out.print("Department: ");
        String department = input.nextLine().trim();

        if (id.isEmpty() || name.isEmpty()
                || qualification.isEmpty() || department.isEmpty()) {
            System.out.println("Required fields cannot be empty.");
            return;
        }

        if (email.isEmpty()) {
            email = null;
        }

        Lecturer lecturer = new Lecturer(
                id, name, qualification, email, department
        );

        if (lecturerDAO.addLecturer(lecturer)) {
            System.out.println("Lecturer registered successfully!");
        } else {
            System.out.println("Failed to register lecturer.");
        }
    }

    // 2. View All Lecturers
    private static void viewAllLecturers() {

        System.out.println("\n--- ALL LECTURERS ---");

        List<Lecturer> lecturers = lecturerDAO.getAllLecturers();

        if (lecturers.isEmpty()) {
            System.out.println("No lecturers found.");
            return;
        }

        for (Lecturer lecturer : lecturers) {

            System.out.println("----------------------------------------");
            System.out.println("Lecturer ID: " + lecturer.getLecturerId());
            System.out.println("Name: " + lecturer.getName());
            System.out.println("Qualification: " + lecturer.getQualification());
            System.out.println("Email: " + lecturer.getEmail());
            System.out.println("Department: " + lecturer.getDepartment());
        }

        System.out.println("----------------------------------------");
        System.out.println("Total Lecturers: " + lecturers.size());
    }

    // 3. Search Lecturer
    private static void searchLecturer() {

        System.out.println("\n--- SEARCH LECTURER ---");

        System.out.print("Enter Lecturer ID: ");
        String id = input.nextLine().trim();

        Lecturer lecturer = lecturerDAO.getLecturerById(id);

        if (lecturer == null) {
            System.out.println("Lecturer not found.");
        } else {
            System.out.println("\nLecturer Details");
            System.out.println("Lecturer ID: " + lecturer.getLecturerId());
            System.out.println("Name: " + lecturer.getName());
            System.out.println("Qualification: " + lecturer.getQualification());
            System.out.println("Email: " + lecturer.getEmail());
            System.out.println("Department: " + lecturer.getDepartment());
        }
    }

    // 4. Update Lecturer
    private static void updateLecturer() {

        System.out.println("\n--- UPDATE LECTURER ---");

        System.out.print("Enter Lecturer ID: ");
        String id = input.nextLine().trim();

        Lecturer lecturer = lecturerDAO.getLecturerById(id);

        if (lecturer == null) {
            System.out.println("Lecturer not found.");
            return;
        }

        System.out.println("Leave a field blank to keep its current value.");

        System.out.print("New Name (" + lecturer.getName() + "): ");
        String name = input.nextLine().trim();

        System.out.print("New Qualification (" + lecturer.getQualification() + "): ");
        String qualification = input.nextLine().trim();

        System.out.print("New Email (" + lecturer.getEmail() + "): ");
        String email = input.nextLine().trim();

        System.out.print("New Department (" + lecturer.getDepartment() + "): ");
        String department = input.nextLine().trim();

        if (!name.isEmpty()) lecturer.setName(name);
        if (!qualification.isEmpty()) lecturer.setQualification(qualification);
        if (!email.isEmpty()) lecturer.setEmail(email);
        if (!department.isEmpty()) lecturer.setDepartment(department);

        if (lecturerDAO.updateLecturer(lecturer)) {
            System.out.println("Lecturer updated successfully!");
        } else {
            System.out.println("Lecturer update failed.");
        }
    }

    // 5. Delete Lecturer
    private static void deleteLecturer() {

        System.out.println("\n--- DELETE LECTURER ---");

        System.out.print("Enter Lecturer ID: ");
        String id = input.nextLine().trim();

        Lecturer lecturer = lecturerDAO.getLecturerById(id);

        if (lecturer == null) {
            System.out.println("Lecturer not found.");
            return;
        }

        System.out.println("Lecturer: " + lecturer.getName());

        System.out.print("Are you sure you want to delete this lecturer? (Y/N): ");
        String confirmation = input.nextLine().trim();

        if (confirmation.equalsIgnoreCase("Y")) {

            if (lecturerDAO.deleteLecturer(id)) {
                System.out.println("Lecturer deleted successfully!");
            } else {
                System.out.println("Unable to delete lecturer.");
                System.out.println("Check whether this lecturer is linked to other records.");
            }

        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}