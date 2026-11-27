package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.UserAccount;
import com.spas.crss.model.Venue;
import com.spas.crss.service.VenueService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class VenueController extends HttpServlet {

    private final VenueService venueService = new VenueService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // GET: Retrieve all venues or filter by availability status
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || "/".equals(path)) {
            getAllVenues(request, response);

        } else if (path.matches("/status/[^/]+")) {
            getVenuesByStatus(request, response);

        } else {
            sendResponse(response, 404, false,
                    "Venue endpoint not found.");
        }
    }

    // POST: Add a new venue
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path != null && !"/".equals(path)) {
            sendResponse(response, 404, false,
                    "Invalid venue creation endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            Venue venue = objectMapper.readValue(
                    request.getInputStream(),
                    Venue.class
            );

            boolean added = venueService.addVenue(user, venue);

            if (!added) {
                sendResponse(response, 400, false,
                        "Venue could not be added.");
                return;
            }

            sendResponse(response, 201, true,
                    "Venue added successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to add venue.");
        }
    }

    // PUT: Update an existing venue
    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/\\d+")) {
            sendResponse(response, 404, false,
                    "Invalid venue update endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            int venueId = Integer.parseInt(path.substring(1));

            Venue venue = objectMapper.readValue(
                    request.getInputStream(),
                    Venue.class
            );

            if (venue.getVenueId() != venueId) {
                sendResponse(response, 400, false,
                        "Venue ID in the URL must match the request body.");
                return;
            }

            boolean updated = venueService.updateVenue(user, venue);

            if (!updated) {
                sendResponse(response, 404, false,
                        "Venue not found or could not be updated.");
                return;
            }

            sendResponse(response, 200, true,
                    "Venue updated successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to update venue.");
        }
    }

    // DELETE: Delete a venue by ID
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        if (path == null || !path.matches("/\\d+")) {
            sendResponse(response, 404, false,
                    "Invalid venue deletion endpoint.");
            return;
        }

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            int venueId = Integer.parseInt(path.substring(1));

            boolean deleted = venueService.deleteVenue(user, venueId);

            if (!deleted) {
                sendResponse(response, 404, false,
                        "Venue not found or could not be deleted.");
                return;
            }

            sendResponse(response, 200, true,
                    "Venue deleted successfully.");

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to delete venue.");
        }
    }

    private void getAllVenues(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            var venues = venueService.getAllVenues(user);

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), venues);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve venues.");
        }
    }

    private void getVenuesByStatus(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount user = getAuthenticatedUser(request, response);

        if (user == null) {
            return;
        }

        try {
            String path = request.getPathInfo();
            String status = path.substring("/status/".length());

            var venues = venueService.getVenuesByStatus(user, status);

            response.setStatus(200);
            objectMapper.writeValue(response.getWriter(), venues);

        } catch (SecurityException e) {
            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {
            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {
            sendResponse(response, 500, false,
                    "Unable to retrieve venues by status.");
        }
    }

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

        Object sessionUser = session.getAttribute("authenticatedUser");

        if (!(sessionUser instanceof UserAccount)) {
            sendResponse(response, 401, false,
                    "Please log in first.");
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
