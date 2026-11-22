
package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.Venue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {

    // 1. Add a new venue
    public boolean addVenue(Venue venue) {

        String sql = "INSERT INTO venue "
                + "(name, building, capacity, type, availability_status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, venue.getName());
            pstmt.setString(2, venue.getBuilding());
            pstmt.setInt(3, venue.getCapacity());
            pstmt.setString(4, venue.getType());
            pstmt.setString(5, venue.getAvailabilityStatus());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding venue: " + e.getMessage());
            return false;
        }
    }

    // 2. Retrieve all venues
    public List<Venue> getAllVenues() {

        List<Venue> venues = new ArrayList<>();

        String sql = "SELECT * FROM venue";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Venue venue = new Venue();

                venue.setVenueId(rs.getInt("venue_id"));
                venue.setName(rs.getString("name"));
                venue.setBuilding(rs.getString("building"));
                venue.setCapacity(rs.getInt("capacity"));
                venue.setType(rs.getString("type"));
                venue.setAvailabilityStatus(
                        rs.getString("availability_status")
                );

                venues.add(venue);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving venues: " + e.getMessage());
        }

        return venues;
    }

    // 3. Retrieve venues by availability status
    public List<Venue> getVenuesByStatus(String status) {

        List<Venue> venues = new ArrayList<>();

        String sql = "SELECT * FROM venue WHERE availability_status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    Venue venue = new Venue();

                    venue.setVenueId(rs.getInt("venue_id"));
                    venue.setName(rs.getString("name"));
                    venue.setBuilding(rs.getString("building"));
                    venue.setCapacity(rs.getInt("capacity"));
                    venue.setType(rs.getString("type"));
                    venue.setAvailabilityStatus(
                            rs.getString("availability_status")
                    );

                    venues.add(venue);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving venues by status: "
                    + e.getMessage());
        }

        return venues;
    }

    // 4. Update venue details
    public boolean updateVenue(Venue venue) {

        String sql = "UPDATE venue SET name = ?, building = ?, "
                + "capacity = ?, type = ?, availability_status = ? "
                + "WHERE venue_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, venue.getName());
            pstmt.setString(2, venue.getBuilding());
            pstmt.setInt(3, venue.getCapacity());
            pstmt.setString(4, venue.getType());
            pstmt.setString(5, venue.getAvailabilityStatus());
            pstmt.setInt(6, venue.getVenueId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating venue: " + e.getMessage());
            return false;
        }
    }

    // 5. Delete a venue
    public boolean deleteVenue(int venueId) {

        String sql = "DELETE FROM venue WHERE venue_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, venueId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting venue: " + e.getMessage());
            return false;
        }
    }
}
