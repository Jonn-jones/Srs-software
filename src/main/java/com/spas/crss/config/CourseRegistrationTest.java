package com.spas.crss.config;

import com.spas.crss.dao.CourseDAO;
import com.spas.crss.model.Course;

import java.util.Scanner;

public class CourseRegistrationTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CourseDAO courseDAO = new CourseDAO();

        System.out.println("====================================");
        System.out.println("         COURSE REGISTRATION");
        System.out.println("====================================");

        // Collect course details
        System.out.print("Enter Course Title: ");
        String courseTitle = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        System.out.print("Enter Credit Hours: ");
        int creditHours = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Course Description: ");
        String description = scanner.nextLine();

        // Create course object
        // Course ID will be generated automatically by MariaDB
        Course course = new Course(
                courseTitle,
                courseCode,
                creditHours,
                description
        );

        // Save course
        boolean success = courseDAO.addCourse(course);

        if (success) {

            System.out.println("\nCourse registered successfully!");

            System.out.println("------------------------------------");
            System.out.println("Generated Course ID: " + course.getCourseId());
            System.out.println("Course Title: " + course.getCourseTitle());
            System.out.println("Course Code: " + course.getCourseCode());
            System.out.println("Credit Hours: " + course.getCreditHours());
            System.out.println("Description: " + course.getDescription());
            System.out.println("------------------------------------");

        } else {

            System.out.println("\nCourse registration failed!");
        }

        System.out.println("====================================");

        scanner.close();
    }
}