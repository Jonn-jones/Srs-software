package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.AcademicSemester;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.AcademicSemesterService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcademicSemesterController extends HttpServlet {

    private final AcademicSemesterService semesterService =
            new AcademicSemesterService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            getAllSemesters(request, response);

        } else if ("/active".equals(path)) {
            getActiveSemester(request, response);

        } else {
            sendResponse(response, 404, false,
                    "Semester endpoint not found.");
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/\\d+/activate")) {

            if (path == null || "/".equals(path)) {
                addSemester(request, response);
            } else {
                sendResponse(response, 404, false,
                        "Invalid semester endpoint.");
            }

            return;
        }

        activateSemester(request, response, path);
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
                    "Invalid semester update endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            int semesterId = Integer.parseInt(path.substring(1));

            AcademicSemester semester = objectMapper.readValue(
                    request.getInputStream(),
                    AcademicSemester.class
            );

            if (semester.getSemesterId() != semesterId) {
                sendResponse(response, 400, false,
                        "Semester ID in the URL must match the request body.");
                return;
            }

            boolean updated = semesterService.updateSemester(user, semester);

            if (!updated) {
                sendResponse(response, 404, false,
                        "Semester could not be updated.");
                return;
            }

            sendResponse(response, 200, true,
                    "Semester updated successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to update semester.");
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
                    "Invalid semester deletion endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            int semesterId = Integer.parseInt(path.substring(1));

            boolean deleted = semesterService.deleteSemester(user, semesterId);

            if (!deleted) {
                sendResponse(response, 404, false,
                        "Semester not found or could not be deleted.");
                return;
            }

            sendResponse(response, 200, true,
                    "Semester deleted successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to delete semester.");
        }
    }

    private void addSemester(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            AcademicSemester semester = objectMapper.readValue(
                    request.getInputStream(),
                    AcademicSemester.class
            );

            int semesterId = semesterService.addSemester(user, semester);

            if (semesterId == -1) {
                sendResponse(response, 400, false,
                        "Semester creation failed.");
                return;
            }

            semester.setSemesterId(semesterId);

            response.setStatus(201);
            objectMapper.writeValue(response.getWriter(), semester);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to create semester.");
        }
    }

    private void getAllSemesters(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            var semesters = semesterService.getAllSemesters(user);

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), semesters);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve semesters.");
        }
    }

    private void getActiveSemester(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            AcademicSemester semester =
                    semesterService.getActiveSemester(user);

            if (semester == null) {
                sendResponse(response, 404, false,
                        "No active semester found.");
                return;
            }

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), semester);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve active semester.");
        }
    }

    private void activateSemester(
            HttpServletRequest request,
            HttpServletResponse response,
            String path
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String idPart = path.substring(1, path.indexOf("/activate"));
            int semesterId = Integer.parseInt(idPart);

            boolean activated =
                    semesterService.activateSemester(user, semesterId);

            if (!activated) {
                sendResponse(response, 404, false,
                        "Semester not found or could not be activated.");
                return;
            }

            sendResponse(response, 200, true,
                    "Semester activated successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to activate semester.");
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
