
package com.spas.crss.config;

import com.spas.crss.dao.CourseAssignmentDAO;
import com.spas.crss.model.CourseAssignment;

public class CourseAssignmentTest {

    public static void main(String[] args) {

        CourseAssignmentDAO dao = new CourseAssignmentDAO();

        System.out.println("===== COURSE ASSIGNMENT TESTING =====");

        // TEST 1: Check existing assignment
        System.out.println("\nTEST 1: Retrieve Existing Assignments");

        for (CourseAssignment a : dao.getAllAssignments()) {
            System.out.println(a);
        }

        // TEST 2: Try assigning DS101 to TEST002
        System.out.println("\nTEST 2: Prevent Duplicate Assignment");

        CourseAssignment duplicateAssignment =
                new CourseAssignment(1, "TEST002", 1);

        boolean duplicateResult =
                dao.assignCourse(duplicateAssignment);

        System.out.println(
                "Duplicate assignment accepted? " + duplicateResult
        );

        // TEST 3: Retrieve assignments by lecturer
        System.out.println("\nTEST 3: Retrieve Lecturer Assignments");

        for (CourseAssignment a :
                dao.getAssignmentsByLecturer("TEST001")) {

            System.out.println(a);
        }

        // TEST 4: Remove existing assignment
        System.out.println("\nTEST 4: Remove Course Assignment");

        boolean removed = dao.removeAssignment(1);

        System.out.println(
                "Assignment removed successfully? " + removed
        );

        // TEST 5: Confirm removal
        System.out.println("\nTEST 5: Verify Assignment Removal");

        for (CourseAssignment a : dao.getAllAssignments()) {
            System.out.println(a);
        }

        System.out.println("\n===== TESTING COMPLETED =====");
    }
}