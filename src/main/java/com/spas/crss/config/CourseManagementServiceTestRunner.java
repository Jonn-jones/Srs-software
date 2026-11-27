package com.spas.crss.config;

import com.spas.crss.dao.CourseDAO;
import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.model.Course;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.CourseManagementService;

import java.util.List;
import java.util.UUID;

public class CourseManagementServiceTestRunner {

    public static void main(String[] args) {
        CourseManagementService courseService = new CourseManagementService();
        CourseDAO courseDAO = new CourseDAO();
        UserAccountDAO userAccountDAO = new UserAccountDAO();

        int createdCourseId = 0;

        try {
            UserAccount codUser =
                    userAccountDAO.getAccountByUsername("Dismus");
            UserAccount studentUser =
                    userAccountDAO.getAccountByUsername("Pixelpp");

            if (codUser == null) {
                throw new RuntimeException("CoD account 'Dismus' was not found.");
            }

            if (studentUser == null) {
                throw new RuntimeException("Student account 'Pixelpp' was not found.");
            }

            String uniqueCode = "T" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 7)
                    .toUpperCase();

            Course testCourse = new Course(
                    "Temporary Test Course",
                    uniqueCode,
                    3,
                    "Temporary course created by the service test runner."
            );

            // 1. Add course
            boolean added = courseService.addCourse(codUser, testCourse);

            if (!added) {
                throw new RuntimeException("Add course test failed.");
            }

            createdCourseId = testCourse.getCourseId();

            if (createdCourseId <= 0) {
                throw new RuntimeException("No course ID was generated.");
            }

            System.out.println("PASS: Add course");

            // 2. Retrieve course by ID
            Course retrievedCourse =
                    courseService.getCourseById(codUser, createdCourseId);

            if (retrievedCourse == null
                    || !uniqueCode.equals(retrievedCourse.getCourseCode())) {
                throw new RuntimeException("Get course by ID test failed.");
            }

            System.out.println("PASS: Get course by ID");

            // 3. Update course
            retrievedCourse.setCourseTitle("Updated Temporary Test Course");

            boolean updated = courseService.updateCourse(codUser, retrievedCourse);

            if (!updated) {
                throw new RuntimeException("Update course test failed.");
            }

            Course updatedCourse =
                    courseService.getCourseById(codUser, createdCourseId);

            if (updatedCourse == null
                    || !"Updated Temporary Test Course"
                            .equals(updatedCourse.getCourseTitle())) {
                throw new RuntimeException("Updated course details were not saved.");
            }

            System.out.println("PASS: Update course");

            // 4. Retrieve all courses
            List<Course> allCourses = courseService.getAllCourses(codUser);
            boolean found = false;

            for (Course course : allCourses) {
                if (course.getCourseId() == createdCourseId) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new RuntimeException("Get all courses test failed.");
            }

            System.out.println("PASS: Get all courses");

            // 5. Validate invalid course details
            try {
                Course invalidCourse = new Course(
                        "",
                        "INVALID",
                        0,
                        "Invalid test course"
                );

                courseService.addCourse(codUser, invalidCourse);
                throw new RuntimeException(
                        "Validation test failed: invalid course was accepted."
                );

            } catch (IllegalArgumentException expected) {
                System.out.println("PASS: Course validation");
            }

            // 6. Verify a student cannot manage courses
            try {
                courseService.getAllCourses(studentUser);
                throw new RuntimeException(
                        "Access test failed: student was allowed to manage courses."
                );

            } catch (SecurityException expected) {
                System.out.println("PASS: Student access denied");
            }

            // 7. Delete temporary course
            boolean deleted =
                    courseService.deleteCourse(codUser, createdCourseId);

            if (!deleted || courseDAO.getCourseById(createdCourseId) != null) {
                throw new RuntimeException("Delete course test failed.");
            }

            createdCourseId = 0;
            System.out.println("PASS: Delete course");

            System.out.println("\nAll Course Management service tests passed.");

        } catch (Exception e) {
            System.err.println("\nCourse Management service test failed:");
            e.printStackTrace();

        } finally {
            // Remove the temporary record if a test failed before deletion.
            if (createdCourseId > 0) {
                try {
                    courseDAO.deleteCourse(createdCourseId);
                    System.out.println("Temporary test course cleaned up.");
                } catch (Exception cleanupError) {
                    System.err.println(
                            "Cleanup failed: " + cleanupError.getMessage()
                    );
                }
            }
        }
    }
}