package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.CoursePrerequisite;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.CoursePrerequisiteService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CoursePrerequisiteController extends HttpServlet {

    private final CoursePrerequisiteService prerequisiteService =
            new CoursePrerequisiteService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // GET: Retrieve prerequisites for a course
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/course/\\d+")) {
            sendResponse(response, 404, false,
                    "Invalid course prerequisite endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            int courseId = Integer.parseInt(
                    path.substring("/course/".length()));

            var prerequisites =
                    prerequisiteService.getPrerequisites(user, courseId);

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), prerequisites);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve course prerequisites.");
        }
    }

    // POST: Add a prerequisite to a course
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path != null && !"/".equals(path)) {
            sendResponse(response, 404, false,
                    "Invalid course prerequisite endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            CoursePrerequisite prerequisite = objectMapper.readValue(
                    request.getInputStream(),
                    CoursePrerequisite.class
            );

            boolean added =
                    prerequisiteService.addPrerequisite(user, prerequisite);

            if (!added) {
                sendResponse(response, 400, false,
                        "Could not add prerequisite. Check that both courses exist and the prerequisite is not already recorded.");
                return;
            }

            sendResponse(response, 201, true,
                    "Course prerequisite added successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to add course prerequisite.");
        }
    }

    // DELETE: Remove a prerequisite from a course
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/course/\\d+/\\d+")) {
            sendResponse(response, 404, false,
                    "Invalid course prerequisite deletion endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String[] parts = path.split("/");
            int courseId = Integer.parseInt(parts[2]);
            int prerequisiteCourseId = Integer.parseInt(parts[3]);

            boolean removed = prerequisiteService.removePrerequisite(
                    user,
                    courseId,
                    prerequisiteCourseId
            );

            if (!removed) {
                sendResponse(response, 404, false,
                        "Prerequisite not found or could not be removed.");
                return;
            }

            sendResponse(response, 200, true,
                    "Course prerequisite removed successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to remove course prerequisite.");
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
