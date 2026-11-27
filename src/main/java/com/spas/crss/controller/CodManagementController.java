package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.Cod;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.CodManagementService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodManagementController extends HttpServlet {

    private final CodManagementService codService =
            new CodManagementService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // GET: Retrieve all CoD profiles or one by ID
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            getAllCods(request, response);
        } else if (path.matches("/[^/]+")) {
            getCodById(request, response);
        } else {
            sendResponse(response, 404, false,
                    "CoD endpoint not found.");
        }
    }

    // POST: Create a CoD profile
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path != null && !"/".equals(path)) {
            sendResponse(response, 404, false,
                    "Invalid CoD creation endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            Cod cod = objectMapper.readValue(
                    request.getInputStream(),
                    Cod.class
            );

            boolean added = codService.addCod(user, cod);

            if (!added) {
                sendResponse(response, 400, false,
                        "CoD profile could not be created.");
                return;
            }

            sendResponse(response, 201, true,
                    "CoD profile created successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to create CoD profile.");
        }
    }

    // PUT: Update a CoD profile
    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/[^/]+")) {
            sendResponse(response, 404, false,
                    "Invalid CoD update endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String codId = path.substring(1);

            Cod cod = objectMapper.readValue(
                    request.getInputStream(),
                    Cod.class
            );

            if (cod.getCodId() == null ||
                    !codId.equals(cod.getCodId())) {
                sendResponse(response, 400, false,
                        "CoD ID in the URL must match the request body.");
                return;
            }

            boolean updated = codService.updateCod(user, cod);

            if (!updated) {
                sendResponse(response, 404, false,
                        "CoD profile could not be updated.");
                return;
            }

            sendResponse(response, 200, true,
                    "CoD profile updated successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to update CoD profile.");
        }
    }

    // DELETE: Delete a CoD profile
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/[^/]+")) {
            sendResponse(response, 404, false,
                    "Invalid CoD deletion endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String codId = path.substring(1);

            boolean deleted = codService.deleteCod(user, codId);

            if (!deleted) {
                sendResponse(response, 404, false,
                        "CoD profile not found or could not be deleted.");
                return;
            }

            sendResponse(response, 200, true,
                    "CoD profile deleted successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to delete CoD profile.");
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

    // Retrieve all CoD profiles
    private void getAllCods(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            var cods = codService.getAllCods(user);

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), cods);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve CoD profiles.");
        }
    }

    // Retrieve one CoD profile by ID
    private void getCodById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String path = request.getPathInfo();
            String codId = path.substring(1);

            Cod cod = codService.getCodById(user, codId);

            if (cod == null) {
                sendResponse(response, 404, false,
                        "CoD profile not found.");
                return;
            }

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), cod);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve CoD profile.");
        }
    }

    // Response configuration
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
