package com.spas.crss.config;

import com.spas.crss.dao.CoursePrerequisiteDAO;
import com.spas.crss.model.CoursePrerequisite;

public class CoursePrerequisiteTest {

    public static void main(String[] args) {

        CoursePrerequisiteDAO dao = new CoursePrerequisiteDAO();

        String studentId = "ct204\\124283\\25";

        System.out.println("===== COURSE PREREQUISITE TESTING =====");

        // Test 1: Retrieve prerequisites for DS102
        System.out.println("\nTEST 1: Retrieve Prerequisites");

        for (CoursePrerequisite p : dao.getPrerequisites(2)) {
            System.out.println(
                    "Course ID: " + p.getCourseId()
                    + ", Prerequisite ID: "
                    + p.getPrerequisiteCourseId()
            );
        }

        // Test 2: Check whether student passed DS101
        System.out.println("\nTEST 2: Check Prerequisite Completion");

        boolean passed = dao.hasPassedPrerequisite(studentId, 1);

        System.out.println("Has student passed DS101? " + passed);

        // Test 3: Check whether student can take DS102
        System.out.println("\nTEST 3: Course Eligibility");

        boolean eligible = dao.canTakeCourse(studentId, 2);

        System.out.println("Can student take DS102? " + eligible);

        // Test 4: Reject a course as its own prerequisite
        System.out.println("\nTEST 4: Prevent Self-Prerequisite");

        boolean selfPrerequisite = dao.addPrerequisite(
                new CoursePrerequisite(1, 1)
        );

        System.out.println("Self-prerequisite added? " + selfPrerequisite);

        System.out.println("\n===== TESTING COMPLETED =====");
    }
}
