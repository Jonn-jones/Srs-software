package com.spas.crss.config;

import com.spas.crss.dao.UserAccountDAO;
import com.spas.crss.dao.StudentCourseHistoryDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.model.StudentCourseHistory;
import com.spas.crss.service.StudentCourseHistoryService;

import java.util.List;

public class StudentCourseHistoryTestRunner {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {

        UserAccountDAO userDAO = new UserAccountDAO();
        StudentCourseHistoryDAO historyDAO =
                new StudentCourseHistoryDAO();

        StudentCourseHistoryService historyService =
                new StudentCourseHistoryService();

        System.out.println("==========================================");
        System.out.println(" SPAS - STUDENT COURSE HISTORY TEST");
        System.out.println("==========================================");

        try {

            // Retrieve existing test accounts
            UserAccount cod =
                    userDAO.getAccountByUsername("Dismus");

            UserAccount student =
                    userDAO.getAccountByUsername("Pixelpp");

            if (cod == null || student == null) {
                throw new IllegalStateException(
                        "Required test accounts were not found.");
            }

            String studentId = student.getStudentId();

            System.out.println("CoD Account: " + cod.getUsername());
            System.out.println("Student Account: " + student.getUsername());
            System.out.println("Student ID: " + studentId);

            int courseId = 1;
            int semesterId = 1;

            // Find an existing test record to make reruns safe
            List<StudentCourseHistory> existingHistory =
                    historyDAO.getHistoryByStudent(studentId);

            StudentCourseHistory history = null;

            for (StudentCourseHistory record : existingHistory) {
                if (record.getCourseId() == courseId
                        && record.getSemesterId() == semesterId) {
                    history = record;
                    break;
                }
            }

            boolean createdNewRecord = false;

            // TEST 1: CoD records a student's result
            if (history == null) {

                history = new StudentCourseHistory(
                        studentId,
                        courseId,
                        semesterId,
                        true
                );

                boolean added = historyService.addHistory(cod, history);

                test(added, "CoD records a student's course result");

                if (!added) {
                    return;
                }

                createdNewRecord = true;

            } else {
                System.out.println(
                        "\nExisting history record found. Reusing it.");
                test(true, "Existing course history record retrieved");
            }

            int historyId = history.getHistoryId();
            boolean originalResult = history.isPassed();

            // TEST 2: Student views their own history
            List<StudentCourseHistory> studentHistory =
                    historyService.viewMyHistory(student);

            test(
                    studentHistory.stream().anyMatch(
                            record -> record.getHistoryId() == historyId),
                    "Student can view their own course history"
            );

            // TEST 3: Student cannot view another student's history
            try {
                historyService.viewStudentHistory(student, "TEST001");

                test(false,
                        "Student cannot view another student's history");

            } catch (SecurityException e) {
                test(true,
                        "Student cannot view another student's history");
            }

            // TEST 4: CoD can view a student's history
            List<StudentCourseHistory> codHistory =
                    historyService.viewStudentHistory(cod, studentId);

            test(
                    codHistory.stream().anyMatch(
                            record -> record.getHistoryId() == historyId),
                    "CoD can view a student's course history"
            );

            // TEST 5: Check whether the student has passed the course
            boolean hasPassed =
                    historyService.hasPassedCourse(
                            student, studentId, courseId);

            test(hasPassed == originalResult,
                    "System correctly checks passed course status");

            // TEST 6: CoD updates the academic result
            boolean updated = historyService.updateResult(
                    cod, historyId, !originalResult);

            test(updated, "CoD can update an academic result");

            // Verify updated result
            boolean updatedResult =
                    historyService.hasPassedCourse(
                            cod, studentId, courseId);

            test(
                    updatedResult == !originalResult,
                    "Updated academic result is reflected in the database"
            );

            // Restore original result so repeated tests remain safe
            boolean restored = historyService.updateResult(
                    cod, historyId, originalResult);

            test(restored, "Original academic result restored");

            System.out.println("\n==========================================");
            System.out.println(" TEST SUMMARY");
            System.out.println("==========================================");
            System.out.println("Passed: " + passedTests);
            System.out.println("Failed: " + failedTests);
            System.out.println("==========================================");

            if (createdNewRecord) {
                System.out.println(
                        "A test history record was created for " + studentId);
            }

        } catch (Exception e) {

            System.out.println("\nTEST EXECUTION ERROR:");
            System.out.println(e.getMessage());

        }
    }

    private static void test(boolean condition, String description) {

        if (condition) {
            System.out.println("[PASSED] " + description);
            passedTests++;
        } else {
            System.out.println("[FAILED] " + description);
            failedTests++;
        }
    }
}