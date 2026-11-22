
package com.spas.crss.config;

import com.spas.crss.dao.VenueDAO;
import com.spas.crss.model.Venue;

public class VenueTest {

    public static void main(String[] args) {

        VenueDAO dao = new VenueDAO();

        System.out.println("===== VENUE MANAGEMENT TESTING =====");

        // TEST 1: Add a venue
        System.out.println("\nTEST 1: Add Venue");

        Venue venue = new Venue(
                "Venue DAO Test 001",
                "Testing Block",
                50,
                "Laboratory",
                "Available"
        );

        boolean added = dao.addVenue(venue);

        System.out.println("Venue added successfully? " + added);


        // TEST 2: Retrieve all venues
        System.out.println("\nTEST 2: Retrieve All Venues");

        for (Venue v : dao.getAllVenues()) {
            System.out.println(v);
        }


        // TEST 3: Retrieve available venues
        System.out.println("\nTEST 3: Retrieve Available Venues");

        for (Venue v : dao.getVenuesByStatus("Available")) {
            System.out.println(v);
        }


        // Find the test venue ID
        int testVenueId = -1;

        for (Venue v : dao.getAllVenues()) {

            if (v.getName().equals("Venue DAO Test 001")
                    && v.getBuilding().equals("Testing Block")) {

                testVenueId = v.getVenueId();
                break;
            }
        }


        // TEST 4: Update venue
        System.out.println("\nTEST 4: Update Venue");

        if (testVenueId != -1) {

            Venue updatedVenue = new Venue(
                    "Venue DAO Test 001",
                    "Testing Block",
                    60,
                    "Laboratory",
                    "Under Maintenance"
            );

            updatedVenue.setVenueId(testVenueId);

            boolean updated = dao.updateVenue(updatedVenue);

            System.out.println("Venue updated successfully? " + updated);

        } else {
            System.out.println("Test venue not found.");
        }


        // TEST 5: Delete venue
        System.out.println("\nTEST 5: Delete Venue");

        if (testVenueId != -1) {

            boolean deleted = dao.deleteVenue(testVenueId);

            System.out.println("Venue deleted successfully? " + deleted);

        }


        // TEST 6: Verify deletion
        System.out.println("\nTEST 6: Verify Venue Deletion");

        for (Venue v : dao.getAllVenues()) {
            System.out.println(v);
        }

        System.out.println("\n===== TESTING COMPLETED =====");
    }
}
