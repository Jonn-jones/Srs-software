package com.spas.crss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spas.crss.model.UserAccount;
import com.spas.crss.service.SystemAdminUserAccountService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SystemAdminController extends HttpServlet {

    private final SystemAdminUserAccountService userAccountService =
            new SystemAdminUserAccountService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // =====================================================
    // 1. GET REQUESTS
    // =====================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();

        if ("/accounts".equals(path)) {

            viewAllAccounts(request, response);

        } else if ("/accounts/search".equals(path)) {

            searchAccountByUsername(request, response);

        } else if ("/accounts/filter".equals(path)) {

            filterAccounts(request, response);

        } else if (path != null && path.matches("/accounts/\\d+")) {

            searchAccountById(request, response);

        } else {

            sendResponse(
                    response,
                    404,
                    false,
                    "System Admin endpoint not found."
            );
        }
    }

    // =====================================================
    // 2. PUT REQUESTS
    // =====================================================

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();

        if (path != null &&
                path.matches("/accounts/\\d+/activate")) {

            updateAccountStatus(request, response, true);

        } else if (path != null &&
                path.matches("/accounts/\\d+/deactivate")) {

            updateAccountStatus(request, response, false);

        } else {

            sendResponse(
                    response,
                    404,
                    false,
                    "System Admin endpoint not found."
            );
        }
    }

    // =====================================================
    // 3. AUTHENTICATION CHECK
    // =====================================================

    private UserAccount getAuthenticatedAdmin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {

            sendResponse(
                    response,
                    401,
                    false,
                    "Please log in first."
            );

            return null;
        }

        Object sessionUser =
                session.getAttribute("authenticatedUser");

        if (!(sessionUser instanceof UserAccount)) {

            sendResponse(
                    response,
                    401,
                    false,
                    "Please log in first."
            );

            return null;
        }

        return (UserAccount) sessionUser;
    }

    // =====================================================
    // 4. VIEW ALL ACCOUNTS
    // =====================================================

    private void viewAllAccounts(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount admin =
                getAuthenticatedAdmin(request, response);

        if (admin == null) {
            return;
        }

        try {

            var accounts =
                    userAccountService.viewAllAccounts(admin);

            response.setStatus(200);

            objectMapper.writeValue(
                    response.getWriter(),
                    accounts
            );

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (Exception e) {

            sendResponse(
                    response,
                    500,
                    false,
                    "Unable to retrieve user accounts."
            );
        }
    }

    // =====================================================
    // 5. SEARCH ACCOUNT BY ID
    // =====================================================

    private void searchAccountById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount admin =
                getAuthenticatedAdmin(request, response);

        if (admin == null) {
            return;
        }

        try {

            String path = request.getPathInfo();

            int accountId = Integer.parseInt(
                    path.substring("/accounts/".length())
            );

            UserAccount account =
                    userAccountService.searchAccountById(
                            admin,
                            accountId
                    );

            if (account == null) {

                sendResponse(
                        response,
                        404,
                        false,
                        "Account not found."
                );

                return;
            }

            response.setStatus(200);

            objectMapper.writeValue(
                    response.getWriter(),
                    account
            );

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(
                    response,
                    500,
                    false,
                    "Unable to search for account."
            );
        }
    }

    // =====================================================
    // 6. SEARCH ACCOUNT BY USERNAME
    // =====================================================

    private void searchAccountByUsername(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount admin =
                getAuthenticatedAdmin(request, response);

        if (admin == null) {
            return;
        }

        try {

            String username =
                    request.getParameter("username");

            UserAccount account =
                    userAccountService.searchAccountByUsername(
                            admin,
                            username
                    );

            if (account == null) {

                sendResponse(
                        response,
                        404,
                        false,
                        "Account not found."
                );

                return;
            }

            response.setStatus(200);

            objectMapper.writeValue(
                    response.getWriter(),
                    account
            );

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(
                    response,
                    500,
                    false,
                    "Unable to search for account."
            );
        }
    }

    // =====================================================
    // 7. FILTER ACCOUNTS BY ROLE AND/OR STATUS
    // =====================================================

    private void filterAccounts(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UserAccount admin =
                getAuthenticatedAdmin(request, response);

        if (admin == null) {
            return;
        }

        try {

            String role = request.getParameter("role");

            String status = request.getParameter("status");

            List<UserAccount> accounts;

            if (role != null && status != null) {

                accounts =
                        userAccountService.getAccountsByRoleAndStatus(
                                admin,
                                role,
                                status
                        );

            } else if (role != null) {

                accounts =
                        userAccountService.getAccountsByRole(
                                admin,
                                role
                        );

            } else if (status != null) {

                accounts =
                        userAccountService.getAccountsByStatus(
                                admin,
                                status
                        );

            } else {

                sendResponse(
                        response,
                        400,
                        false,
                        "Provide a role, status, or both to filter accounts."
                );

                return;
            }

            response.setStatus(200);

            objectMapper.writeValue(
                    response.getWriter(),
                    accounts
            );

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(
                    response,
                    500,
                    false,
                    "Unable to filter accounts."
            );
        }
    }

    // =====================================================
    // 8. ACTIVATE OR DEACTIVATE ACCOUNT
    // =====================================================

    private void updateAccountStatus(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean activate
    ) throws IOException {

        UserAccount admin =
                getAuthenticatedAdmin(request, response);

        if (admin == null) {
            return;
        }

        try {

            String path = request.getPathInfo();

            String[] parts = path.split("/");

            int accountId = Integer.parseInt(parts[2]);

            boolean updated;

            if (activate) {

                updated =
                        userAccountService.activateAccount(
                                admin,
                                accountId
                        );

            } else {

                updated =
                        userAccountService.deactivateAccount(
                                admin,
                                accountId
                        );
            }

            if (!updated) {

                sendResponse(
                        response,
                        400,
                        false,
                        activate
                                ? "Account could not be activated."
                                : "Account could not be deactivated."
                );

                return;
            }

            sendResponse(
                    response,
                    200,
                    true,
                    activate
                            ? "Account activated successfully."
                            : "Account deactivated successfully."
            );

        } catch (SecurityException e) {

            sendResponse(response, 403, false, "Access denied.");

        } catch (IllegalArgumentException e) {

            sendResponse(response, 400, false, e.getMessage());

        } catch (Exception e) {

            sendResponse(
                    response,
                    500,
                    false,
                    "Unable to update account status."
            );
        }
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

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("success", success);

        result.put("message", message);

        response.setStatus(status);

        objectMapper.writeValue(
                response.getWriter(),
                result
        );
    }
}