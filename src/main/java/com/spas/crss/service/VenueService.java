package com.spas.crss.service;

import com.spas.crss.dao.VenueDAO;
import com.spas.crss.model.UserAccount;
import com.spas.crss.model.Venue;
import com.spas.crss.security.AccessControl;
import com.spas.crss.security.Permission;

import java.util.List;

public class VenueService {

    private final VenueDAO venueDAO;
    private final AccessControl accessControl;

    public VenueService() {
        this.venueDAO = new VenueDAO();
        this.accessControl = new AccessControl();
    }

    public boolean addVenue(UserAccount user, Venue venue) {
        checkManagementPermission(user);
        validateVenue(venue, false);

        return venueDAO.addVenue(venue);
    }

    public List<Venue> getAllVenues(UserAccount user) {
        checkAuthenticated(user);

        return venueDAO.getAllVenues();
    }

    public List<Venue> getVenuesByStatus(UserAccount user, String status) {
        checkAuthenticated(user);

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Venue availability status is required.");
        }

        return venueDAO.getVenuesByStatus(status.trim());
    }

    public boolean updateVenue(UserAccount user, Venue venue) {
        checkManagementPermission(user);
        validateVenue(venue, true);

        return venueDAO.updateVenue(venue);
    }

    public boolean deleteVenue(UserAccount user, int venueId) {
        checkManagementPermission(user);

        if (venueId <= 0) {
            throw new IllegalArgumentException(
                    "A valid venue ID is required.");
        }

        return venueDAO.deleteVenue(venueId);
    }

    private void checkManagementPermission(UserAccount user) {
        if (!accessControl.hasPermission(
                user, Permission.MANAGE_VENUES)) {
            throw new SecurityException(
                    "You do not have permission to manage venues.");
        }
    }

    private void checkAuthenticated(UserAccount user) {
        if (user == null) {
            throw new SecurityException(
                    "Authentication is required.");
        }
    }

    private void validateVenue(Venue venue, boolean requireId) {
        if (venue == null) {
            throw new IllegalArgumentException(
                    "Venue details are required.");
        }

        if (requireId && venue.getVenueId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid venue ID is required.");
        }

        if (venue.getName() == null || venue.getName().trim().isEmpty()
                || venue.getBuilding() == null
                || venue.getBuilding().trim().isEmpty()
                || venue.getType() == null
                || venue.getType().trim().isEmpty()
                || venue.getAvailabilityStatus() == null
                || venue.getAvailabilityStatus().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Venue name, building, type, and availability status are required.");
        }

        if (venue.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                    "Venue capacity must be greater than zero.");
        }
    }
}
