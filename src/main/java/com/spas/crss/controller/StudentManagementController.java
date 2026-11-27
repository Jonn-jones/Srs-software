package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.Student;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.StudentManagementService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudentManagementController extends HttpServlet {

    private final StudentManagementService studentService =
            new StudentManagementService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // =====================================================
    // 1. GET REQUESTS
    // =====================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {

            getAllStudents(request, response);

        } else if (path.matches("/[^/]+")) {

            getStudentById(request, response);

        } else {

            sendResponse(response, 404, false,
                    "Student endpoint not found.");
        }
    }

    // =====================================================
    // 2. POST REQUEST - REGISTER STUDENT
    // =====================================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path != null && !"/".equals(path)) {

            sendResponse(response, 404, false,
                    "Invalid student registration endpoint.");

            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {

            Student student = objectMapper.readValue(
                    request.getInputStream(),
                    Student.class
            );

            boolean added = studentService.addStudent(user, student);

            if (!added) {

                sendResponse(response, 400, false,
                        "Student registration failed.");

                return;
            }

            sendResponse(response, 201, true,
                    "Student registered successfully.");

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(response, 500, false,
                    "Unable to register student.");
        }
    }

    // =====================================================
    // 3. PUT REQUEST - UPDATE STUDENT
    // =====================================================

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/[^/]+")) {

            sendResponse(response, 404, false,
                    "Invalid student update endpoint.");

            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {

            String studentId = path.substring(1);

            Student student = objectMapper.readValue(
                    request.getInputStream(),
                    Student.class
            );

            if (student.getStudentId() == null ||
                    !studentId.equals(student.getStudentId())) {

                sendResponse(response, 400, false,
                        "Student ID in the URL must match the request body.");

                return;
            }

            boolean updated =
                    studentService.updateStudent(user, student);

            if (!updated) {

                sendResponse(response, 404, false,
                        "Student could not be updated.");

                return;
            }

            sendResponse(response, 200, true,
                    "Student updated successfully.");

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(response, 500, false,
                    "Unable to update student.");
        }
    }

    // =====================================================
    // 4. DELETE REQUEST - DELETE STUDENT
    // =====================================================

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/[^/]+")) {

            sendResponse(response, 404, false,
                    "Invalid student deletion endpoint.");

            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {

            String studentId = path.substring(1);

            boolean deleted =
                    studentService.deleteStudent(user, studentId);

            if (!deleted) {

                sendResponse(response, 404, false,
                        "Student not found or could not be deleted.");

                return;
            }

            sendResponse(response, 200, true,
                    "Student deleted successfully.");

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(response, 500, false,
                    "Unable to delete student.");
        }
    }

    // =====================================================
    // 5. AUTHENTICATION CHECK
    // =====================================================

    private UserAccount getAuthenticatedUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {

            sendResponse(response, 401, false,
                    "Please log in first.");

            return null;
        }

        Object sessionUser =
                session.getAttribute("authenticatedUser");

        if (!(sessionUser instanceof UserAccount)) {

            sendResponse(response, 401, false,
                    "Please log in first.");

            return null;
        }

        return (UserAccount) sessionUser;
    }

    // =====================================================
    // 6. VIEW ALL STUDENTS
    // =====================================================

    private void getAllStudents(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {

            var students = studentService.getAllStudents(user);

            response.setStatus(200);

            objectMapper.writeValue(
                    response.getWriter(),
                    students
            );

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {

            sendResponse(response, 500, false,
                    "Unable to retrieve students.");
        }
    }

    // =====================================================
    // 7. RETRIEVE STUDENT BY ID
    // =====================================================

    private void getStudentById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {

            String path = request.getPathInfo();

            String studentId = path.substring(1);

            Student student =
                    studentService.getStudentById(user, studentId);

            if (student == null) {

                sendResponse(response, 404, false,
                        "Student not found.");

                return;
            }

            response.setStatus(200);

            objectMapper.writeValue(
                    response.getWriter(),
                    student
            );

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(response, 500, false,
                    "Unable to retrieve student.");
        }
    }

    // =====================================================
    // 8. RESPONSE CONFIGURATION
    // =====================================================

    private void prepareResponse(HttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    // =====================================================
    // 9. STANDARD JSON RESPONSE
    // =====================================================

    private void sendResponse(
            HttpServletResponse response,
            int status,
            boolean success,
            String message
    ) throws IOException {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("success", success);
        result.put("message", message);

        response.setStatus(status);

        objectMapper.writeValue(
                response.getWriter(),
                result
        );
    }
}
