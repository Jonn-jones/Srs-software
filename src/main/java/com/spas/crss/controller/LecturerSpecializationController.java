package com.spas.crss.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.TeachingSpecialization;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.LecturerSpecializationService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LecturerSpecializationController extends HttpServlet {

    private final LecturerSpecializationService specializationService =
            new LecturerSpecializationService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            getAllSpecializations(request, response);

        } else if (path.matches("/lecturer/[^/]+")) {
            getLecturerSpecializations(request, response);

        } else {
            sendResponse(response, 404, false,
                    "Specialization endpoint not found.");
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            addSpecialization(request, response);

        } else if ("/assign".equals(path)) {
            assignSpecialization(request, response);

        } else {
            sendResponse(response, 404, false,
                    "Invalid specialization endpoint.");
        }
    }

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null
                || !path.matches("/lecturer/[^/]+/\\d+")) {

            sendResponse(response, 404, false,
                    "Invalid specialization removal endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String[] parts = path.split("/");

            String lecturerId = parts[2];
            int specializationId = Integer.parseInt(parts[3]);

            boolean removed =
                    specializationService.removeSpecialization(
                            user,
                            lecturerId,
                            specializationId
                    );

            if (!removed) {
                sendResponse(response, 404, false,
                        "Specialization assignment not found.");
                return;
            }

            sendResponse(response, 200, true,
                    "Specialization removed from lecturer successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to remove specialization.");
        }
    }

    // CREATE SPECIALIZATION
    private void addSpecialization(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            TeachingSpecialization specialization =
                    objectMapper.readValue(
                            request.getInputStream(),
                            TeachingSpecialization.class
                    );

            int specializationId =
                    specializationService.addSpecialization(
                            user,
                            specialization
                    );

            if (specializationId <= 0) {
                sendResponse(response, 400, false,
                        "Specialization creation failed.");
                return;
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("message",
                    "Specialization created successfully.");
            result.put("specializationId", specializationId);

            response.setStatus(201);
            objectMapper.writeValue(response.getWriter(), result);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to create specialization.");
        }
    }

    // RETRIEVE ALL SPECIALIZATIONS
    private void getAllSpecializations(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            List<TeachingSpecialization> specializations =
                    specializationService.getAllSpecializations(user);

            response.setStatus(200);
            objectMapper.writeValue(
                    response.getWriter(),
                    specializations
            );

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve specializations.");
        }
    }

    // ASSIGN SPECIALIZATION TO LECTURER
    private void assignSpecialization(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            JsonNode body = objectMapper.readTree(
                    request.getInputStream()
            );

            if (body == null
                    || !body.hasNonNull("lecturerId")
                    || !body.hasNonNull("specializationId")) {

                sendResponse(response, 400, false,
                        "Lecturer ID and specialization ID are required.");
                return;
            }

            String lecturerId = body.get("lecturerId").asText();
            int specializationId =
                    body.get("specializationId").asInt();

            String result =
                    specializationService.assignSpecialization(
                            user,
                            lecturerId,
                            specializationId
                    );

            boolean success = result != null
                    && result.toLowerCase().contains("success");

            sendResponse(
                    response,
                    success ? 200 : 400,
                    success,
                    result == null
                            ? "Unable to assign specialization."
                            : result
            );

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to assign specialization.");
        }
    }

    // VIEW A LECTURER'S SPECIALIZATIONS
    private void getLecturerSpecializations(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String path = request.getPathInfo();
            String lecturerId = path.substring("/lecturer/".length());

            List<TeachingSpecialization> specializations =
                    specializationService.getLecturerSpecializations(
                            user,
                            lecturerId
                    );

            response.setStatus(200);
            objectMapper.writeValue(
                    response.getWriter(),
                    specializations
            );

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve lecturer specializations.");
        }
    }

    // AUTHENTICATION
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

    // RESPONSE CONFIGURATION
    private void prepareResponse(HttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    // STANDARD JSON RESPONSE
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
