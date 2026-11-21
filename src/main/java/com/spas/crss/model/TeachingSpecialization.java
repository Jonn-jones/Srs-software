package com.spas.crss.model;

public class TeachingSpecialization {

    private int specializationId;
    private String specializationName;
    private String description;

    public TeachingSpecialization() {
    }

    public TeachingSpecialization(int specializationId,
                                  String specializationName,
                                  String description) {

        this.specializationId = specializationId;
        this.specializationName = specializationName;
        this.description = description;
    }

    public int getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(int specializationId) {
        this.specializationId = specializationId;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TeachingSpecialization{" +
                "specializationId=" + specializationId +
                ", specializationName='" + specializationName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}