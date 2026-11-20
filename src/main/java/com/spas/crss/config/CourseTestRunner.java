package com.spas.crss.config;

import com.spas.crss.dao.CourseDAO;
import com.spas.crss.model.Course;

import java.util.List;

public class CourseTestRunner {

    public static void main(String[] args) {

        CourseDAO courseDAO = new CourseDAO();

        System.out.println("==========================================");
        System.out.println("       SPAS CRSS COURSE RECORDS");
        System.out.println("==========================================");

        List<Course> courses = courseDAO.getAllCourses();

        if (courses.isEmpty()) {

            System.out.println("No courses found.");

        } else {

            for (Course course : courses) {

                System.out.println("\nCourse ID: " + course.getCourseId());
                System.out.println("Course Title: " + course.getCourseTitle());
                System.out.println("Course Code: " + course.getCourseCode());
                System.out.println("Credit Hours: " + course.getCreditHours());
                System.out.println("Description: " + course.getDescription());

                System.out.println("------------------------------------------");
            }
        }

        System.out.println("Total Courses: " + courses.size());

        System.out.println("==========================================");
    }
}