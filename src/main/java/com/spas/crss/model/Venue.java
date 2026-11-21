
package com.spas.crss.model;

public class Venue {

    private int venueId;
    private String name;
    private String building;
    private int capacity;
    private String type;
    private String availabilityStatus;

    // Default constructor
    public Venue() {
    }

    // Constructor without venue ID
    public Venue(String name, String building,
                 int capacity, String type,
                 String availabilityStatus) {

        this.name = name;
        this.building = building;
        this.capacity = capacity;
        this.type = type;
        this.availabilityStatus = availabilityStatus;
    }

    // Getters and Setters
    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "venueId=" + venueId +
                ", name='" + name + '\'' +
                ", building='" + building + '\'' +
                ", capacity=" + capacity +
                ", type='" + type + '\'' +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                '}';
    }
}

