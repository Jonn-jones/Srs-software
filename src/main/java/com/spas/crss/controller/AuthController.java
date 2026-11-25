package com.spas.crss.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.AuthenticationService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AuthController extends HttpServlet {

    private final AuthenticationService authenticationService =
            new AuthenticationService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();

        if ("/login".equals(path)) {
            login(request, response);

        } else if ("/logout".equals(path)) {
            logout(request, response);

        } else {
            sendResponse(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    false,
                    "Authentication endpoint not found."
            );
        }
    }

    private void login(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        JsonNode body = objectMapper.readTree(request.getInputStream());

        String username = body.hasNonNull("username")
                ? body.get("username").asText()
                : null;

        String password = body.hasNonNull("password")
                ? body.get("password").asText()
                : null;

        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {

            sendResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "Username and password are required."
            );
            return;
        }

        UserAccount account =
                authenticationService.login(username, password);

        if (account == null) {
            sendResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    false,
                    "Invalid credentials or inactive account."
            );
            return;
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("authenticatedUser", account);

        Map<String, Object> user = new LinkedHashMap<>();

        user.put("accountId", account.getAccountId());
        user.put("username", account.getUsername());
        user.put("role", account.getRole());
        user.put("accountStatus", account.getAccountStatus());

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("success", true);
        result.put("message", "Login successful.");
        result.put("user", user);

        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), result);
    }

    private void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("authenticatedUser") == null) {

            sendResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    false,
                    "No active session found."
            );
            return;
        }

        session.invalidate();

        sendResponse(
                response,
                HttpServletResponse.SC_OK,
                true,
                "Logout successful."
        );
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

