package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.CourseAssignment;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.CourseAssignmentService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CourseAssignmentController extends HttpServlet {

    private final CourseAssignmentService assignmentService =
            new CourseAssignmentService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // GET: Retrieve all assignments or assignments by lecturer
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            getAllAssignments(request, response);

        } else if (path.matches("/lecturer/[^/]+")) {
            getAssignmentsByLecturer(request, response);

        } else {
            sendResponse(response, 404, false,
                    "Course assignment endpoint not found.");
        }
    }

    // POST: Assign a course to a lecturer
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path != null && !"/".equals(path)) {
            sendResponse(response, 404, false,
                    "Invalid course assignment endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            CourseAssignment assignment = objectMapper.readValue(
                    request.getInputStream(),
                    CourseAssignment.class
            );

            boolean assigned =
                    assignmentService.assignCourse(user, assignment);

            if (!assigned) {
                sendResponse(response, 400, false,
                        "Course assignment failed. Check that the course, lecturer, and semester exist and that the assignment is not duplicated.");
                return;
            }

            sendResponse(response, 201, true,
                    "Course assigned successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to assign course.");
        }
    }

    // DELETE: Remove an assignment by assignment ID
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/\\d+")) {
            sendResponse(response, 404, false,
                    "Invalid course assignment deletion endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            int assignmentId = Integer.parseInt(path.substring(1));

            boolean removed =
                    assignmentService.removeAssignment(user, assignmentId);

            if (!removed) {
                sendResponse(response, 404, false,
                        "Assignment not found or could not be removed.");
                return;
            }

            sendResponse(response, 200, true,
                    "Course assignment removed successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to remove course assignment.");
        }
    }

    // Retrieve all course assignments
    private void getAllAssignments(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            var assignments =
                    assignmentService.getAllAssignments(user);

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), assignments);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve course assignments.");
        }
    }

    // Retrieve assignments for a specific lecturer
    private void getAssignmentsByLecturer(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String path = request.getPathInfo();
            String lecturerId =
                    path.substring("/lecturer/".length());

            var assignments =
                    assignmentService.getAssignmentsByLecturer(
                            user,
                            lecturerId
                    );

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), assignments);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve lecturer assignments.");
        }
    }

    // Authentication check
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

    // Configure JSON response
    private void prepareResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    // Standard JSON response
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
        objectMapper.writeValue(response.getWriter(), result);
    }
}
