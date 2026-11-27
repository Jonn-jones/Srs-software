package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.Lecturer;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.LecturerManagementService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class LecturerManagementController extends HttpServlet {

    private final LecturerManagementService lecturerService =
            new LecturerManagementService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // GET: Retrieve all lecturers or one lecturer by ID
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            getAllLecturers(request, response);
        } else if (path.matches("/[^/]+")) {
            getLecturerById(request, response);
        } else {
            sendResponse(response, 404, false,
                    "Lecturer endpoint not found.");
        }
    }

    // POST: Add lecturer
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path != null && !"/".equals(path)) {
            sendResponse(response, 404, false,
                    "Invalid lecturer registration endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            Lecturer lecturer = objectMapper.readValue(
                    request.getInputStream(),
                    Lecturer.class
            );

            boolean added = lecturerService.addLecturer(user, lecturer);

            if (!added) {
                sendResponse(response, 400, false,
                        "Lecturer registration failed.");
                return;
            }

            sendResponse(response, 201, true,
                    "Lecturer added successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to add lecturer.");
        }
    }

    // PUT: Update lecturer
    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/[^/]+")) {
            sendResponse(response, 404, false,
                    "Invalid lecturer update endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String lecturerId = path.substring(1);

            Lecturer lecturer = objectMapper.readValue(
                    request.getInputStream(),
                    Lecturer.class
            );

            if (lecturer.getLecturerId() == null ||
                    !lecturerId.equals(lecturer.getLecturerId())) {

                sendResponse(response, 400, false,
                        "Lecturer ID in the URL must match the request body.");
                return;
            }

            boolean updated =
                    lecturerService.updateLecturer(user, lecturer);

            if (!updated) {
                sendResponse(response, 404, false,
                        "Lecturer not found or could not be updated.");
                return;
            }

            sendResponse(response, 200, true,
                    "Lecturer updated successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to update lecturer.");
        }
    }

    // DELETE: Delete lecturer
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/[^/]+")) {
            sendResponse(response, 404, false,
                    "Invalid lecturer deletion endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String lecturerId = path.substring(1);

            boolean deleted =
                    lecturerService.deleteLecturer(user, lecturerId);

            if (!deleted) {
                sendResponse(response, 404, false,
                        "Lecturer not found or could not be deleted.");
                return;
            }

            sendResponse(response, 200, true,
                    "Lecturer deleted successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to delete lecturer.");
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

    // Retrieve all lecturers
    private void getAllLecturers(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            var lecturers = lecturerService.getAllLecturers(user);

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), lecturers);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve lecturers.");
        }
    }

    // Retrieve lecturer by ID
    private void getLecturerById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String lecturerId =
                    request.getPathInfo().substring(1);

            Lecturer lecturer =
                    lecturerService.getLecturerById(user, lecturerId);

            if (lecturer == null) {
                sendResponse(response, 404, false,
                        "Lecturer not found.");
                return;
            }

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), lecturer);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");
        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());
        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve lecturer.");
        }
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
