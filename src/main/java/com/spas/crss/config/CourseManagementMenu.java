package com.spas.crss.config;

import com.spas.crss.dao.CourseDAO;
import com.spas.crss.model.Course;

import java.util.List;
import java.util.Scanner;

public class CourseManagementMenu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CourseDAO courseDAO = new CourseDAO();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n======================================");
            System.out.println("       SPAS CRSS COURSE MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Register New Course");
            System.out.println("2. View All Courses");
            System.out.println("3. Search Course");
            System.out.println("4. Update Course");
            System.out.println("5. Delete Course");
            System.out.println("0. Exit");
            System.out.println("======================================");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Enter a number.");
                choice = -1;
                continue;
            }

            switch (choice) {

                case 1:
                    registerCourse();
                    break;

                case 2:
                    viewAllCourses();
                    break;

                case 3:
                    searchCourse();
                    break;

                case 4:
                    updateCourse();
                    break;

                case 5:
                    deleteCourse();
                    break;

                case 0:
                    System.out.println("Exiting Course Management...");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 0);

        scanner.close();
    }

    // Register Course
    private static void registerCourse() {

        System.out.println("\n========== REGISTER COURSE ==========");

        System.out.print("Enter Course Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String code = scanner.nextLine();

        System.out.print("Enter Credit Hours: ");
        int creditHours;

        try {
            creditHours = Integer.parseInt(scanner.nextLine());

            if (creditHours <= 0) {
                System.out.println("Credit hours must be greater than zero.");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid credit hours.");
            return;
        }

        System.out.print("Enter Course Description: ");
        String description = scanner.nextLine();

        Course course = new Course(
                title, code, creditHours, description
        );

        if (courseDAO.addCourse(course)) {
            System.out.println("\nCourse registered successfully!");
            System.out.println("Generated Course ID: " + course.getCourseId());
        } else {
            System.out.println("\nCourse registration failed!");
        }
    }

    // View All Courses
    private static void viewAllCourses() {

        System.out.println("\n========== ALL COURSES ==========");

        List<Course> courses = courseDAO.getAllCourses();

        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        for (Course course : courses) {

            System.out.println("\nCourse ID: " + course.getCourseId());
            System.out.println("Title: " + course.getCourseTitle());
            System.out.println("Code: " + course.getCourseCode());
            System.out.println("Credit Hours: " + course.getCreditHours());
            System.out.println("Description: " + course.getDescription());

            System.out.println("--------------------------------");
        }

        System.out.println("Total Courses: " + courses.size());
    }

    // Search Course
    private static void searchCourse() {

        System.out.println("\n========== SEARCH COURSE ==========");

        int id = readCourseId();

        if (id == -1) {
            return;
        }

        Course course = courseDAO.getCourseById(id);

        if (course != null) {
            System.out.println("\nCourse Found!");
            System.out.println("Course ID: " + course.getCourseId());
            System.out.println("Title: " + course.getCourseTitle());
            System.out.println("Code: " + course.getCourseCode());
            System.out.println("Credit Hours: " + course.getCreditHours());
            System.out.println("Description: " + course.getDescription());

        } else {
            System.out.println("Course not found.");
        }
    }

    // Update Course
    private static void updateCourse() {

        System.out.println("\n========== UPDATE COURSE ==========");

        int id = readCourseId();

        if (id == -1) {
            return;
        }

        Course existingCourse = courseDAO.getCourseById(id);

        if (existingCourse == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.println("Leave a field blank to keep its current value.");

        System.out.print("New Course Title (" 
                + existingCourse.getCourseTitle() + "): ");

        String title = scanner.nextLine();

        if (!title.isBlank()) {
            existingCourse.setCourseTitle(title);
        }

        System.out.print("New Course Code (" 
                + existingCourse.getCourseCode() + "): ");

        String code = scanner.nextLine();

        if (!code.isBlank()) {
            existingCourse.setCourseCode(code);
        }

        System.out.print("New Credit Hours (" 
                + existingCourse.getCreditHours() + "): ");

        String hours = scanner.nextLine();

        if (!hours.isBlank()) {
            try {
                int creditHours = Integer.parseInt(hours);

                if (creditHours <= 0) {
                    System.out.println("Credit hours must be positive.");
                    return;
                }

                existingCourse.setCreditHours(creditHours);

            } catch (NumberFormatException e) {
                System.out.println("Invalid credit hours.");
                return;
            }
        }

        System.out.print("New Description (" 
                + existingCourse.getDescription() + "): ");

        String description = scanner.nextLine();

        if (!description.isBlank()) {
            existingCourse.setDescription(description);
        }

        if (courseDAO.updateCourse(existingCourse)) {
            System.out.println("Course updated successfully!");
        } else {
            System.out.println("Course update failed.");
        }
    }

    // Delete Course
    private static void deleteCourse() {

        System.out.println("\n========== DELETE COURSE ==========");

        int id = readCourseId();

        if (id == -1) {
            return;
        }

        Course course = courseDAO.getCourseById(id);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.println("Course: " + course.getCourseTitle());
        System.out.print("Are you sure you want to delete it? (Y/N): ");

        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("Y")) {

            if (courseDAO.deleteCourse(id)) {
                System.out.println("Course deleted successfully!");
            } else {
                System.out.println("Course deletion failed.");
            }

        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // Read Course ID
    private static int readCourseId() {

        System.out.print("Enter Course ID: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            if (id <= 0) {
                System.out.println("Course ID must be positive.");
                return -1;
            }

            return id;

        } catch (NumberFormatException e) {
            System.out.println("Invalid Course ID.");
            return -1;
        }
    }
}