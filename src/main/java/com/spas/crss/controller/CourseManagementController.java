package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.Course;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.CourseManagementService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CourseManagementController extends HttpServlet {

    private final CourseManagementService courseService =
            new CourseManagementService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            getAllCourses(request, response);
        } else if (path.matches("/\\d+")) {
            getCourseById(request, response);
        } else {
            sendResponse(response, 404, false, "Course endpoint not found.");
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path != null && !"/".equals(path)) {
            sendResponse(response, 404, false,
                    "Invalid course creation endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        try {
            Course course = objectMapper.readValue(
                    request.getInputStream(),
                    Course.class
            );

            boolean added = courseService.addCourse(user, course);

            if (!added) {
                sendResponse(response, 400, false, "Course creation failed.");
                return;
            }

            response.setStatus(201);
            objectMapper.writeValue(response.getWriter(), course);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false, "Unable to create course.");
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/\\d+")) {
            sendResponse(response, 404, false,
                    "Invalid course update endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        try {
            int courseId = Integer.parseInt(path.substring(1));

            Course course = objectMapper.readValue(
                    request.getInputStream(),
                    Course.class
            );

            if (course.getCourseId() != courseId) {
                sendResponse(response, 400, false,
                        "Course ID in the URL must match the request body.");
                return;
            }

            boolean updated = courseService.updateCourse(user, course);

            if (!updated) {
                sendResponse(response, 404, false,
                        "Course could not be updated.");
                return;
            }

            sendResponse(response, 200, true,
                    "Course updated successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false, "Unable to update course.");
        }
    }

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/\\d+")) {
            sendResponse(response, 404, false,
                    "Invalid course deletion endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        try {
            int courseId = Integer.parseInt(path.substring(1));

            boolean deleted = courseService.deleteCourse(user, courseId);

            if (!deleted) {
                sendResponse(response, 404, false,
                        "Course not found or could not be deleted.");
                return;
            }

            sendResponse(response, 200, true,
                    "Course deleted successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false, "Unable to delete course.");
        }
    }

    private void getAllCourses(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        try {
            var courses = courseService.getAllCourses(user);
            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), courses);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve courses.");
        }
    }

    private void getCourseById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        try {
            int courseId = Integer.parseInt(
                    request.getPathInfo().substring(1)
            );

            Course course = courseService.getCourseById(user, courseId);

            if (course == null) {
                sendResponse(response, 404, false, "Course not found.");
                return;
            }

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), course);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve course.");
        }
    }

    private UserAccount getAuthenticatedUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            sendResponse(response, 401, false, "Please log in first.");
            return null;
        }

        Object sessionUser = session.getAttribute("authenticatedUser");

        if (!(sessionUser instanceof UserAccount)) {
            sendResponse(response, 401, false, "Please log in first.");
            return null;
        }

        return (UserAccount) sessionUser;
    }

    private void prepareResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

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
