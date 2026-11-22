package com.spas.crss.config;

import com.spas.crss.dao.CourseDAO;
import com.spas.crss.model.Course;

public class CourseLookupTestRunner {

    public static void main(String[] args) {

        CourseDAO courseDAO = new CourseDAO();

        System.out.println("================================");
        System.out.println("COURSE LOOKUP TEST");
        System.out.println("================================");

        Course course = courseDAO.getCourseById(1);

        if (course != null) {

            System.out.println("Course found!");
            System.out.println("Course ID: " + course.getCourseId());
            System.out.println("Course Title: " + course.getCourseTitle());
            System.out.println("Course Code: " + course.getCourseCode());

        } else {

            System.out.println("Course ID 1 was not found.");
        }

        System.out.println("================================");
    }
}
